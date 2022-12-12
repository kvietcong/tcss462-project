#!/bin/bash

echo -n "Enter Experiment or \"all\": "
read EXPERIMENT

echo -n "Enter Loop Amount: "
read LOOPS

echo -n "Enter Sleep Amount in Seconds: "
read SLEEP_AMOUNT

re='^[0-9]+$'
if ! [[ $LOOPS =~ $re ]] ; then
    echo "error: Loops ($LOOPS) is not a number" >&2; exit 1
fi
if ! [[ $SLEEP_AMOUNT =~ $re ]] ; then
    echo "error: Sleep amount ($SLEEP_AMOUNT) is not a number" >&2; exit 1
fi

echo "Looping $LOOPS time(s)"
echo Sleeping for $SLEEP_AMOUNT seconds between experiments

for i in $(seq $LOOPS)
do
    echo
    if [ "$EXPERIMENT" == "all" ]; then
        for EX in "greyscale" "flipVertical" "soften" "soften x 8" "soften x 16"
        do
            echo Loop $i of $EX "(All)"

            (trap 'kill 0' SIGINT; python3 faas_runner.py -f functions/imageProcessingJava.json -e "experiments/$EX.json" & python3 faas_runner.py -f functions/imageProcessingJavaScript.json -e "experiments/$EX.json" & wait) &> /dev/null

            echo Sleeping $SLEEP_AMOUNT seconds after loop $i of $EX
            sleep $SLEEP_AMOUNT
        done
    else
        echo Loop $i of $EXPERIMENT "(Single)"
        (trap 'kill 0' SIGINT; python3 faas_runner.py -f functions/imageProcessingJava.json -e "experiments/$EXPERIMENT.json" & python3 faas_runner.py -f functions/imageProcessingJavaScript.json -e "experiments/$EXPERIMENT.json" & wait) &> /dev/null

        echo Sleeping $SLEEP_AMOUNT seconds after loop $i of $EXPERIMENT
        sleep $SLEEP_AMOUNT
    fi
done
