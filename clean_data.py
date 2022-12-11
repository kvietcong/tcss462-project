from statistics import mean, stdev
from json import loads, dumps
from glob import glob
from sys import argv


def get_results():
    runtime_by_language_by_newcontainer_by_filter_by_image = {}
    for run_output_filepath in glob(
        "./**/test/history/**/*.json", recursive=True
    ):
        with open(run_output_filepath) as run_output:
            run_json = loads(run_output.read())

            payload = run_json["payload"] = loads(
                run_json["payload"].replace('"; "', '", "')
            )  # Super fragile way to fix payload output
            filter = run_json["filter"] = payload["filter"]
            runtime = int(run_json["runtime"])
            newcontainer = int(run_json["newcontainer"])
            image = payload["key"]
            filename = run_json["lang"]

            runtime_by_language_by_newcontainer_by_filter_by_image \
                .setdefault(image, {}) \
                .setdefault(filter, {}) \
                .setdefault(newcontainer, {}) \
                .setdefault(filename, []) \
                .append(runtime)

            # print(run_json)

    return {
        image: {
            filter: {
                "cold" if newcontainer else "warm": {
                    language: {
                        "average_ms": mean(runtimes),
                        "standard_deviation":
                            stdev(runtimes) if len(runtimes) > 1 else "N/A",
                        "sample_size": len(runtimes),
                    }
                    for language, runtimes
                    in runtime_by_language.items()
                }
                for newcontainer, runtime_by_language
                in runtime_by_language_by_newcontainer.items()
            }
            for filter, runtime_by_language_by_newcontainer
            in runtime_by_language_by_newcontainer_by_filter.items()
        }
        for image, runtime_by_language_by_newcontainer_by_filter
        in runtime_by_language_by_newcontainer_by_filter_by_image.items()
    }


results = get_results()

if len(argv) > 1:
    filename = argv[1]
    print(f"Getting values for file: {filename}")
    results = results[filename]

if len(argv) > 2:
    filter = argv[2]
    print(f"Getting values for filter: {filter}")
    results = results[filter]

if len(argv) > 3:
    container_state = argv[3]
    print(f"Getting values for {container_state} container")
    results = results[container_state]

print("Runtimes")
print(dumps(results, indent=2))
