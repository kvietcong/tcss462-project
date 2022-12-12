for i in 1 2 3 4 5 6 7 8 9 10
do
    for EXPERIMENT in "mountains" "vietfood" "deathstar" "husky"
    do
        python3 faas_runner.py -f functions/imageProcessing.json -e experiments/$EXPERIMENT.json
        echo Sleeping after loop $i of $EXPERIMENT
        sleep 1000
    done
done
