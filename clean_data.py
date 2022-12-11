from json import loads
from glob import glob
import pandas as pd
import numpy as np


def clean_data():
    runs = []
    for run_output_filepath in glob(
        "./**/test/history/**/*.json", recursive=True
    ):
        with open(run_output_filepath) as run_output:
            run_json = loads(run_output.read())
            runs.append(run_json)

    df = pd.DataFrame(runs)
    df = df.rename(columns={
        "lang": "language"
    })
    df = df.assign(
        payload=lambda df: df["payload"]
        .map(lambda payload: loads(payload.replace('"; "', '", "'))),

        runtime=lambda df: df["runtime"].astype(float),
        roundTripTime=lambda df: df["roundTripTime"].astype(float),
        newcontainer=lambda df: df["newcontainer"].astype(int),

        filter=lambda df: df["payload"].map(lambda payload: payload["filter"]),
        filename=lambda df: df["payload"].map(lambda payload: payload["key"]),
        container_state=lambda df: np.where(
            df["newcontainer"] == 1, "cold", "hot"
        ),
    )

    grouped_columns = [
        # "cpuType",
        "filename", "filter", "container_state", "language",
    ]
    runtime_results = df.groupby(grouped_columns)["runtime"] \
        .agg(["mean", "std", "count"])
    runtime_results = runtime_results.rename(columns={
        "mean": "average runtime (ms)",
        "std": "standard deviation",
        "count": "sample size",
    })
    print("RUN TIME")
    print(runtime_results.to_string())

    return df


if __name__ == "__main__":
    clean_data()
