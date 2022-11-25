const { loadImage, createCanvas } = require("canvas");

// TODO: Clean up the code! Get better variable names, don't use fancy stuff, and abstract Image into class.
const filters = {
    greyscale: image => {
        const pixels = image.data;
        const width = image.width;
        const height = image.height;

        console.log(width, height)

        for (let row = 0; row < height; row++) {
            for (let col = 0; col < width; col++) {
                const [rI, gI, bI, _aI] = [
                    4 * ((row * width) + col),
                    4 * ((row * width) + col) + 1,
                    4 * ((row * width) + col) + 2,
                    4 * ((row * width) + col) + 3,
                ];

                const gray = Math.floor((pixels[rI] + pixels[gI] + pixels[bI]) / 3);

                pixels[rI] = gray;
                pixels[gI] = gray;
                pixels[bI] = gray;
            }
        }
    },
    flipVertical: image => {
        const pixels = image.data;
        const width = image.width;
        const height = image.height;

        for (let row = 0; row < Math.floor(height / 2); row++) {
            for (let col = 0; col < width; col++) {
                const [rIa, gIa, bIa, _aIa] = [
                    4 * ((row * width) + col),
                    4 * ((row * width) + col) + 1,
                    4 * ((row * width) + col) + 2,
                    4 * ((row * width) + col) + 3,
                ];
                const [rIb, gIb, bIb, _aIb] = [
                    4 * (((height - row - 1) * width) + col),
                    4 * (((height - row - 1) * width) + col) + 1,
                    4 * (((height - row - 1) * width) + col) + 2,
                    4 * (((height - row - 1) * width) + col) + 3,
                ];
                // console.log(rIa, rIb)

                const [tR, tG, tB] = [pixels[rIa], pixels[gIa], pixels[bIa]]
                pixels[rIa] = pixels[rIb];
                pixels[gIa] = pixels[gIb];
                pixels[bIa] = pixels[bIb];
                pixels[rIb] = tR;
                pixels[gIb] = tG;
                pixels[bIb] = tB;
            }
        }
    },
    flipHorizontal: image => {
        const pixels = image.data;
        const width = image.width;
        const height = image.height;

        for (let row = 0; row < height; row++) {
            for (let col = 0; col < Math.floor(width / 2); col++) {
                const [rIa, gIa, bIa, _aIa] = [
                    4 * ((row * width) + col),
                    4 * ((row * width) + col) + 1,
                    4 * ((row * width) + col) + 2,
                    4 * ((row * width) + col) + 3,
                ];
                const [rIb, gIb, bIb, _aIb] = [
                    4 * ((row * width) + (width - col - 1)),
                    4 * ((row * width) + (width - col - 1)) + 1,
                    4 * ((row * width) + (width - col - 1)) + 2,
                    4 * ((row * width) + (width - col - 1)) + 3,
                ];
                // console.log(rIa, rIb)

                const [tR, tG, tB] = [pixels[rIa], pixels[gIa], pixels[bIa]]
                pixels[rIa] = pixels[rIb];
                pixels[gIa] = pixels[gIb];
                pixels[bIa] = pixels[bIb];
                pixels[rIb] = tR;
                pixels[gIb] = tG;
                pixels[bIb] = tB;
            }
        }
    },
    soften: image => { },
};

const applyFilter = (filter, image) => console.log(`Applying "${filter}" to Image!`) || filters[filter](image);

(async () => {
    const args = process.argv.slice(2);

    const imagePath = args[1] || "./image.jpg";
    const image = await loadImage(imagePath);

    const canvas = createCanvas(image.width, image.height);
    const context = canvas.getContext("2d");
    context.drawImage(image, 0, 0, image.width, image.height);
    const imageData = context.getImageData(0, 0, image.width, image.height);
    console.log("BEFORE:", imageData);
    applyFilter(args[0] || "greyscale", imageData);
    console.log("AFTER:", imageData);
    context.putImageData(imageData, 0, 0);

    const fs = require("fs");
    const out = fs.createWriteStream("./test.png");
    canvas.createPNGStream().pipe(out);
    out.on("finish", () => console.log("The PNG file was created."));
})();

/**
 * Define your FaaS Function here.
 * Each platform handler will call and pass parameters to this function.
 *
 * @param request A JSON object provided by the platform handler.
 * @param context A platform specific object used to communicate with the platform.
 * @returns A JSON object to use as a response.
 */
module.exports = async (request, context) => {
    //Import the module and collect data
    const inspector = new (require("./Inspector"))();
    inspector.inspectAll();

    console.log("test", context.test)

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
