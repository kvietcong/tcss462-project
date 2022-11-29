const { read } = require("jimp");
const { writeFileSync, readFileSync } = require("fs");
const { S3Client, GetObjectCommand, PutObjectCommand } = require("@aws-sdk/client-s3");

const createImage = async path => {
    const image = await read(path);

    const rows = image.bitmap.height;
    const cols = image.bitmap.width;

    const rawData = image.bitmap.data;

    const getPixel = (row, col) => {
        const baseIndex = 4 * ((row * cols) + col);
        return {
            red: rawData[baseIndex + 0],
            green: rawData[baseIndex + 1],
            blue: rawData[baseIndex + 2],
            alpha: rawData[baseIndex + 3],
        };
    };
    const setPixel = (row, col, rgba) => {
        const baseIndex = 4 * ((row * cols) + col);
        const { red, green, blue, alpha } = rgba;
        rawData[baseIndex + 0] = red;
        rawData[baseIndex + 1] = green;
        rawData[baseIndex + 2] = blue;
        rawData[baseIndex + 3] = alpha;
    };

    return {
        getPixel, setPixel,
        get rows() { return rows },
        get cols() { return cols },
        swapPixels(rowA, colA, rowB, colB) {
            const tempPixel = getPixel(rowA, colA);
            setPixel(rowA, colA, getPixel(rowB, colB));
            setPixel(rowB, colB, tempPixel);
        },
        writeToFile(path) {
            return image.quality(80).writeAsync(path);
        },
        applyFilter(filter) {
            filter(this);
            return this;
        },
    };
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
    // soften: image => { },
};

// (async () => {
//     try {
//         const args = process.argv.slice(2);
//
//         const path = args[1] || "C:/Users/minec/Downloads/image.jpg";
//         const filter = args[0] || "greyscale";
//         const image = await createImage(path)
//         image.applyFilter(filters[filter]).writeToFile("C:/Users/minec/Downloads/test.png");
//     } catch { }
// })();

module.exports.handler = async (request, _context) => {
    const Inspector = require("./Inspector");
    const inspector = new Inspector();
    inspector.inspectAll();

    const { key, filter, newKey } = request;

    try {
        const s3 = new S3Client({ region: "us-east-2" });
        const getResponse = await s3.send(new GetObjectCommand({
            Bucket: "test.bucket.462-562.f22.kv",
            Key: key,
        }));
        const imageData = await getResponse.Body.transformToByteArray()
        writeFileSync("/tmp/original", imageData);

        const image = await createImage("/tmp/original")
        await image.applyFilter(filters[filter]).writeToFile("/tmp/new");

        await s3.send(new PutObjectCommand({
            Bucket: "test.bucket.462-562.f22.kv",
            Key: newKey || `${new Date().getTime()}.jpg`,
            Body: readFileSync("/tmp/new"),
        }));
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
