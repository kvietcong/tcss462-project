const { createWriteStream } = require("fs");
const { createCanvas, loadImage } = require("canvas");

const createImage = async path => {
    const image = await loadImage(path);

    const rows = image.height;
    const cols = image.width;

    const canvas = createCanvas(cols, rows);
    const context = canvas.getContext("2d");
    context.drawImage(image, 0, 0, cols, rows);

    const imageData = context.getImageData(0, 0, cols, rows);
    context.putImageData(imageData, 0, 0);

    const rawData = imageData.data;
    console.log(rawData);

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
        imageData.data[baseIndex + 0] = red;
        imageData.data[baseIndex + 1] = green;
        imageData.data[baseIndex + 2] = blue;
        imageData.data[baseIndex + 3] = alpha;
    };
    const createPNGStream = () => {
        context.putImageData(imageData, 0, 0);
        return canvas.createPNGStream();
    };

    return {
        getPixel, setPixel, createPNGStream,
        get rows() { return rows },
        get cols() { return cols },
        swapPixels(rowA, colA, rowB, colB) {
            const tempPixel = getPixel(rowA, colA);
            setPixel(rowA, colA, getPixel(rowB, colB));
            setPixel(rowB, colB, tempPixel);
        },
        writeToFile(path) {
            const out = createWriteStream(path);
            createPNGStream().pipe(out);
            out.on("finish", () => console.log(`Image written to "${path}"`));
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
//     const args = process.argv.slice(2);
//
//     const path = args[1] || "./image.jpg";
//     const filter = args[0] || "greyscale";
//     const image = await createImage(path)
//     image.applyFilter(filters[filter]).writeToFile("./test.png");
// })();

module.exports = {
    handler: async (request, _context) => {
        const Inspector = require("./Inspector");
        const inspector = new Inspector();
        inspector.inspectAll();

        //Add custom message and finish the function
        if (typeof request.name !== "undefined" && request.name !== null) {
            inspector.addAttribute("message", "Hello " + request.name + "!");
        } else {
            inspector.addAttribute("message", "Hello World!");
        }

        inspector.inspectAllDeltas();
        //inspector.pushS3("saafdump", context)
        return inspector.finish();
    },
};
