#!/bin/bash

echo -n "Enter Experiment or \"all\": "
read EXPERIMENT

if [ "$EXPERIMENT" == "" ]; then
    EXPERIMENT="all"
fi

echo -n "Enter Loop Amount: "
read LOOPS

echo -n "Enter Sleep Amount in Seconds: "
read SLEEP_AMOUNT

echo -n "Input between runs? (y/N) "
read PAUSE

re='^[0-9]+$'
if ! [[ $LOOPS =~ $re ]] ; then
    echo "error: Loops ($LOOPS) is not a number! Defaulting to 1."
    LOOPS=1
fi
if ! [[ $SLEEP_AMOUNT =~ $re ]] ; then
    echo "error: Sleep amount ($SLEEP_AMOUNT) is not a number! Defaulting to 0."
    SLEEP_AMOUNT=0
fi

echo "Looping $LOOPS time(s)"
echo Sleeping for $SLEEP_AMOUNT seconds between experiments

for i in $(seq $LOOPS)
do
    echo
    if [ "$EXPERIMENT" == "all" ]; then
        for EX in "greyscale" "flipVertical" "soften" "soften x 4" "soften x 8"
        do
            if [[ "$PAUSE" == "y" ]]; then
                echo -n "Press ENTER to continue"
                read
            fi

            echo Loop $i of $EX "(All)"

            (trap 'kill 0' SIGINT; python3 faas_runner.py -f functions/imageProcessingJava.json -e "experiments/$EX.json" & python3 faas_runner.py -f functions/imageProcessingJavaScript.json -e "experiments/$EX.json" & wait) > .output.txt

            printf \\a
            echo Sleeping $SLEEP_AMOUNT seconds after loop $i of $EX
            sleep $SLEEP_AMOUNT
        done
    else
        if [[ "$PAUSE" == "y" ]]; then
            echo -n "Press ENTER to continue"
            read
        fi

        echo Loop $i of $EXPERIMENT "(Single)"
        (trap 'kill 0' SIGINT; python3 faas_runner.py -f functions/imageProcessingJava.json -e "experiments/$EXPERIMENT.json" & python3 faas_runner.py -f functions/imageProcessingJavaScript.json -e "experiments/$EXPERIMENT.json" & wait) > .output.txt

        printf \\a
        echo Sleeping $SLEEP_AMOUNT seconds after loop $i of $EXPERIMENT
        sleep $SLEEP_AMOUNT
    fi
done
