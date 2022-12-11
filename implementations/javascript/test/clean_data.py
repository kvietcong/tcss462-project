from csv import DictReader
from json import loads, dumps

important_stats = [
    "newcontainer", "runtime", "cpuType", "roundTripTime", "filter"
]
with open("runs.csv") as file:
    runs = DictReader(file.readlines())
    runtime_by_newcontainer_by_filter_by_image = {}

    for run in runs:
        # Super fragile way to fix payload output
        payload = run["payload"] = loads(run["payload"].replace("\";", "\","))
        filter = run["filter"] = payload["filter"]
        runtime = int(run["runtime"])
        newcontainer = int(run["newcontainer"])
        image = payload["key"]

        runtime_by_newcontainer_by_filter_by_image \
            .setdefault(image, {}) \
            .setdefault(filter, {}) \
            .setdefault(newcontainer, []) \
            .append(runtime)
        # print({stat: run[stat] for stat in important_stats})

    print(dumps({
        image: {
            filter: {
                "cold" if newcontainer else "warm": {
                    "avg": sum(runtimes)/len(runtimes),
                    "sample_size": len(runtimes),
                }
                for newcontainer, runtimes in runtime_by_newcontainer.items()
            }
            for filter, runtime_by_newcontainer
            in runtime_by_newcontainer_by_filter.items()
        }
        for image, runtime_by_newcontainer_by_filter
        in runtime_by_newcontainer_by_filter_by_image.items()
    }, indent=2))
