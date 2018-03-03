package com.company;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    // Max number of routes, max time duration in minutes constants

    public static final int VARIANT = 1;
    public static final int MAX_ROUTES = 120;
    public static final int MAX_DURATION = 600;
    public static final double START_TIMES_DENSITY_FACTOR = 0.90;

    // We applied roulette selection with different probability per station

    public static final int LEVEL5_ROULETTE_MAXSPOT = 27;
    public static final int LEVEL4_ROULETTE_MAXSPOT = 50;
    public static final int LEVEL3_ROULETTE_MAXSPOT = 70;
    public static final int LEVEL2_ROULETTE_MAXSPOT = 87;
    public static final int LEVEL1_ROULETTE_MAXSPOT = 100;


    // Max number of routes per station level in a simulation

    public static final int LEVEL5_MAXROUTES = 8;
    public static final int LEVEL4_MAXROUTES = 7;
    public static final int LEVEL3_MAXROUTES = 6;
    public static final int LEVEL2_MAXROUTES = 5;
    public static final int LEVEL1_MAXROUTES = 4;

    public static final String START_TIME = "06:06";

    public static void main(String[] args) {

        // Arraylists for start stations, finish stations and request times

        ArrayList<Integer> startStations = new ArrayList<>();
        ArrayList<Integer> finishStations = new ArrayList<>();
        ArrayList<String> requestTime = new ArrayList<>();

        // Setup station level arrays with their ids

        int[] level5Stations = {9,23,26};
        int[] level4Stations = {6,7,8,15};
        int[] level3Stations = {2,4,13,16,22};
        int[] level2Stations = {5,10,12,17,20,21};
        int[] level1Stations = {1,3,11,14,18,19,24,25,27};

        // Setup user array with user ids
        int[] users = {17,18,19,20,21,22,23,24,25};

        // Initialize variables

        boolean firstStation = true;
        int selectedStation = 0;
        int counterleve5Stations = 0;
        int counterleve4Stations = 0;
        int counterleve3Stations = 0;
        int counterleve2Stations = 0;
        int counterleve1Stations = 0;

        ////////////////////////////////////////////////////////////////
        // Step 1: Generate start stations and finish stations        //
        // and save them in arraylists: startStations, finishStations //
        ////////////////////////////////////////////////////////////////

        for (int i = 0; i < MAX_ROUTES ; i++) {

            boolean generated = false;
            int selection = randInt(0, LEVEL1_ROULETTE_MAXSPOT);

            if (selection < LEVEL5_ROULETTE_MAXSPOT) {

                if (counterleve5Stations <= LEVEL5_MAXROUTES * (MAX_ROUTES/30)) {
                    selectedStation = level5Stations[randInt(0, level5Stations.length-1)];
                    counterleve5Stations++;
                    generated = true;
                }

            } else if (selection < LEVEL4_ROULETTE_MAXSPOT) {

                if (counterleve4Stations <= LEVEL4_MAXROUTES * (MAX_ROUTES/30)) {
                    selectedStation = level4Stations[randInt(0, level4Stations.length-1)];
                    counterleve4Stations++;
                    generated = true;
                }

            } else if (selection < LEVEL3_ROULETTE_MAXSPOT) {

                if (counterleve3Stations <= LEVEL3_MAXROUTES * (MAX_ROUTES/30)) {
                    selectedStation = level3Stations[randInt(0, level3Stations.length-1)];
                    counterleve3Stations++;
                    generated = true;
                }

            } else if (selection < LEVEL2_ROULETTE_MAXSPOT) {

                if (counterleve2Stations <= LEVEL2_MAXROUTES * (MAX_ROUTES/30)) {
                    selectedStation = level2Stations[randInt(0, level2Stations.length-1)];
                    counterleve2Stations++;
                    generated = true;
                }

            } else {

                if (counterleve1Stations <= LEVEL1_MAXROUTES * (MAX_ROUTES/30)) {
                    selectedStation = level1Stations[randInt(0, level1Stations.length-1)];
                    counterleve1Stations++;
                    generated = true;
                }

            }

            if (generated) {

                if (firstStation) startStations.add(selectedStation);
                else finishStations.add(selectedStation);

                firstStation = !firstStation;

            } else i--;
        }

        /////////////////////////////////////////////////////
        // Step 2: Generate start request times for routes //
        // and save them in arraylist: requestTime         //
        /////////////////////////////////////////////////////

        boolean shortMode = true;

        for (int i = 0; i < MAX_ROUTES ; i++) {

            if (shortMode) {

                int randomTime = randInt(0, MAX_DURATION);
                requestTime.add(startTime(START_TIME, (randomTime)));
                shortMode = !shortMode;

            } else {

                int min = (int)Math.round(MAX_DURATION * START_TIMES_DENSITY_FACTOR);
                int randomTime = randInt(min, MAX_DURATION);
                requestTime.add(startTime(START_TIME , (randomTime)));
                shortMode = !shortMode;
            }
        }

        ///////////////////////////////////////////////////
        // Step 3: Generate string data for the XML file //
        // and save them in string: dataForXML           //
        ///////////////////////////////////////////////////

        String dataForXML = "";

        int j = 0;
        for (int i = 0; i < MAX_ROUTES ; i++) {

            if (j <= users.length-1) {

                if (i < (MAX_ROUTES/2)) {
                    dataForXML = dataForXML + "<request><message>" +
                            users[j] + " " +
                            startStations.get(i) + " " +
                            finishStations.get(i) + " " +
                            requestTime.get(i) + "</message></request>" + "\n";
                    j++;

                } else {
                    dataForXML = dataForXML + "<request><message>" +
                            users[j] + " " +
                            startStations.get(i-MAX_ROUTES/2) + " " +
                            finishStations.get(i-MAX_ROUTES/2) + " " +
                            requestTime.get(i) + "</message></request>" + "\n";
                    j++;
                }

            } else {

                j = 0;
                i--;
            }
        }

        ////////////////////////////////////////////////////////////
        // Step 4: Create a new XML file and save it to hard disk //
        ////////////////////////////////////////////////////////////

        try {

            File file = new File("simulation-" +
                                                "variant" + VARIANT +
                                                "-routes" + MAX_ROUTES +
                                                "-minutes" + MAX_DURATION +
                                                "-density" + START_TIMES_DENSITY_FACTOR + ".xml");

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("<?xml version=\"1.0\"?>\n");
            fileWriter.write("<evsharing-simulations>\n");
            fileWriter.write(dataForXML);
            fileWriter.write("</evsharing-simulations>");
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    // Calculate random integer with min and max
    public static int randInt(int min, int max) {

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    // Calculate next start time for route request
    public static String startTime(String startTime, int randomTime) {

        String formattedTime = "";

        try {

            // Set specific time for calendar 1 and add calculated trip duration

            Date time1 = new SimpleDateFormat("HH:mm").parse(startTime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);
            calendar1.add(Calendar.MINUTE, randomTime);

            // Format time and convert to string

            SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
            formattedTime = format1.format(calendar1.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedTime;
    }
}