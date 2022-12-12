#!/bin/bash

echo -n "Enter Experiment or \"all\": "
read EXPERIMENT

echo -n "Enter Loop Amount: "
read LOOPS

echo -n "Enter Sleep Amount in Seconds: "
read SLEEP_AMOUNT

echo "Looping $LOOPS time(s)"
echo Sleeping for $SLEEP_AMOUNT seconds between experiments

for i in $(seq $LOOPS)
do
    if [ $EXPERIMENT == "all" ]; then
        for EXPERIMENT
        in "greyscale" "flipVertical" "soften" "soften x 8" "soften x 16"
        do
            (trap 'kill 0' SIGINT; python3 faas_runner.py -f functions/imageProcessingJava.json -e "experiments/$EXPERIMENT.json" & python3 faas_runner.py -f functions/imageProcessingJavaScript.json -e "experiments/$EXPERIMENT.json" & wait)

            echo Sleeping $SLEEP_AMOUNT seconds after loop $i of $EXPERIMENT
            sleep $SLEEP_AMOUNT
        done
    else
        (trap 'kill 0' SIGINT; python3 faas_runner.py -f functions/imageProcessingJava.json -e "experiments/$EXPERIMENT.json" & python3 faas_runner.py -f functions/imageProcessingJavaScript.json -e "experiments/$EXPERIMENT.json" & wait)

        echo Sleeping $SLEEP_AMOUNT seconds after loop $i of $EXPERIMENT
        sleep $SLEEP_AMOUNT
    fi
done
