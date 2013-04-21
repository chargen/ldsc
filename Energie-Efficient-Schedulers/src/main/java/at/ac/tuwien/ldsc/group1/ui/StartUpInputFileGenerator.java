package at.ac.tuwien.ldsc.group1.ui;

import at.ac.tuwien.ldsc.group1.application.InputFileGenerator;

public class StartUpInputFileGenerator {

    public static void main(String[] args) {
        int numberOfRandomItems = 0;
        String filename;
        if(args.length > 0) {
            numberOfRandomItems = Integer.parseInt(args[0]);
        }
        if(args.length > 1) {
            filename = args[1];
        } else {
            System.out.println("Wrong format: StartUpInputFileGenerator number filename");
            return;
        }

        InputFileGenerator gen = new InputFileGenerator();
        System.out.println("Generatoring file" + filename + " with " + numberOfRandomItems + " random lines");
        gen.generateFile(numberOfRandomItems, filename);
    }
}
