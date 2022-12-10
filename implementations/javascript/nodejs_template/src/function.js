const { read, } = require("jimp");
const { writeFileSync, readFileSync } = require("fs");
const { S3Client, GetObjectCommand, PutObjectCommand } = require("@aws-sdk/client-s3");

class Image {
    constructor(jimpImage) {
        this._jimpImage = jimpImage;
    }

    get jimpImage() { return this._jimpImage }
    get bitmap() { return this.jimpImage.bitmap }

    get rows() { return this.bitmap.height }
    get cols() { return this.bitmap.width }

    getPixel(row, col) {
        const baseIndex = 4 * ((row * this.cols) + col);
        return {
            red: this.bitmap.data[baseIndex + 0],
            green: this.bitmap.data[baseIndex + 1],
            blue: this.bitmap.data[baseIndex + 2],
            alpha: this.bitmap.data[baseIndex + 3],
        };
    }

    setPixel(row, col, rgba) {
        const baseIndex = 4 * ((row * this.cols) + col);
        const { red, green, blue, alpha } = rgba;
        this.bitmap.data[baseIndex + 0] = red;
        this.bitmap.data[baseIndex + 1] = green;
        this.bitmap.data[baseIndex + 2] = blue;
        if (alpha !== undefined) this.bitmap.data[baseIndex + 3] = alpha;
        return this;
    }

    swapPixels(rowA, colA, rowB, colB) {
        const tempPixel = this.getPixel(rowA, colA);
        this.setPixel(rowA, colA, this.getPixel(rowB, colB));
        this.setPixel(rowB, colB, tempPixel);
        return this;
    }

    writeToFileAsync(path) {
        return this.jimpImage.quality(80).writeAsync(path);
    }

    applyFilter(filter) {
        filter(this);
        return this;
    }

    clone() {
        return new Image(this.jimpImage.clone());
    }
}

const createImage = async path => {
    const image = await read(path);
    return new Image(image);
};

const filters = {
    greyscale: image => {
        for (let row = 0; row < image.rows; row++) {
            for (let col = 0; col < image.cols; col++) {
                const pixel = image.getPixel(row, col);
                const gray = Math.floor((pixel.red + pixel.green + pixel.blue) / 3);
                pixel.red = pixel.green = pixel.blue = gray;
                image.setPixel(row, col, pixel);
            }
        }
    },
    flipVertical: image => {
        for (let row = 0; row < Math.floor(image.rows / 2); row++)
            for (let col = 0; col < image.cols; col++)
                image.swapPixels(row, col, image.rows - row - 1, col);
    },
    flipHorizontal: image => {
        for (let row = 0; row < image.rows; row++)
            for (let col = 0; col < Math.floor(image.cols / 2); col++)
                image.swapPixels(row, col, row, image.cols - col - 1);
    },
    soften: image => {
        const weights = [
            [1, 2, 1],
            [2, 4, 2],
            [1, 2, 1],
        ];

        const sum = weights.reduce(
            (accumulated, row) =>
                accumulated + row.reduce(
                    (rowSum, el) => rowSum + el,
                    0
                ),
            0
        ) || 1;

        const weight = (image, weights, sum) => {
            const originalImage = image.clone();
            for (let row = 0; row < image.rows; row++) {
                for (let col = 0; col < image.cols; col++) {
                    let red = 0;
                    let green = 0;
                    let blue = 0;

                    for (let j = Math.max(0, row - 1); j <= Math.min(row + 1, image.rows - 1); j++) {
                        for (let i = Math.max(0, col - 1); i <= Math.min(col + 1, image.cols - 1); i++) {
                            const pixel = originalImage.getPixel(j, i);
                            const weight = weights[row - j + 1][col - i + 1];
                            red = red + pixel.red * weight;
                            green = green + pixel.green * weight;
                            blue = blue + pixel.blue * weight;
                        }
                    }

                    red = Math.min(Math.floor(red / sum), 255);
                    green = Math.min(Math.floor(green / sum), 255);
                    blue = Math.min(Math.floor(blue / sum), 255);

                    image.setPixel(row, col, { red, green, blue });
                }
            }
        };
        weight(image, weights, sum);
    },
};

// (async () => {
//     try {
//         const args = process.argv.slice(2);
//
//         const filterInputs = args[0] || "greyscale";
//         const inPath = args[1] || "C:/Users/minec/Downloads/image.jpg";
//         const outPath = args[2] || "C:/Users/minec/Downloads/test.png";
//
//         console.log(`Loading ${inPath}`);
//         const image = await createImage(inPath);
//         for (const filterInput of filterInputs.split(",")) {
//             const inputs = filterInput.split(".");
//             const filter = inputs.length === 1 ? inputs[0] : inputs[1];
//             const repeats = inputs.length === 1 ? 1 : parseInt(inputs[0]);
//             console.log(`Applying ${filter} ${repeats} time(s)`);
//             for (let i = 1; i <= repeats; i++) {
//                 if (i % 5 === 0) console.log(`  Step ${i}`);
//                 image.applyFilter(filters[filter]);
//             }
//         }
//         console.log(`Writing to ${outPath}`);
//         image.writeToFileAsync(outPath);
//     } catch (error) {
//         console.error(error);
//     }
// })();

module.exports.handler = async (request, _context) => {
    const Inspector = require("./Inspector");
    const inspector = new Inspector();
    inspector.inspectAll();

    const { key, filter } = request;
    const repeats = request.repeats || 1;
    const bucket = request.bucket || "test.bucket.462-562.f22.kv";
    const newKey = request.newKey || `${new Date().getTime()}.jpg`;

    try {
        const s3 = new S3Client({ region: "us-east-2" });
        const getResponse = await s3.send(new GetObjectCommand({
            Bucket: bucket,
            Key: key,
        }));
        const imageData = await getResponse.Body.transformToByteArray()
        writeFileSync("/tmp/original", imageData);

        const image = await createImage("/tmp/original")
        for (let i = 0; i < repeats; i++)
            image.applyFilter(filters[filter]);
        await image.writeToFileAsync("/tmp/new");

        await s3.send(new PutObjectCommand({
            Bucket: bucket,
            Key: newKey,
            Body: readFileSync("/tmp/new"),
        }));

        inspector.addAttribute("key", newKey);
        inspector.addAttribute("bucket", bucket);
    } catch (error) {
        console.error(error);
    }

    //Add custom message and finish the function
    if (typeof request.name !== "undefined" && request.name !== null) {
        inspector.addAttribute("message", "Hello " + request.name + "!");
    } else {
        inspector.addAttribute("message", "Hello World!");
    }

    inspector.inspectAllDeltas();
    //inspector.pushS3("saafdump", context)
    return inspector.finish();
};