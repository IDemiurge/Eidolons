package eidolons.libgdx.launch;

import main.data.filesys.PathFinder;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GpuTester {

    private static Integer dedicatedMemory=2000;
    private static Integer sharedMemory;
    private static boolean measured;

    public static Integer getDedicatedMemory() {
        return dedicatedMemory;
    }

    public static Integer getSharedMemory() {
        return sharedMemory;
    }

    private static void applyAdjustment() {
        if (dedicatedMemory<=2000){
            CoreEngine.setWeakGpu(true);
        }

    }
    public static void test() {
        if (!CoreEngine.isWindows()){
            return;
        }
        new Thread(() -> {
            try {
                String filePath = PathFinder.getRootPath() + "/gpu info.txt";
                if (!FileManager.isFile(filePath)){
                // Use "dxdiag /t" variant to redirect output to a given file
                ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "dxdiag", "/t", filePath);
                System.out.println("-- Executing dxdiag command --");
                Process p = pb.start();
                p.waitFor();
                }

                BufferedReader br = new BufferedReader(new FileReader(filePath));
                String line;
                String info = "";
                while ((line = br.readLine()) != null) {
                    if (line.trim().startsWith("Card name:") || line.trim().startsWith("Current Mode:")
                            || line.trim().startsWith("Dedicated Memory:") || line.trim().startsWith("Shared Memory:")
                    ) {
                        if (line.trim().startsWith("Dedicated Memory:")) {
                            dedicatedMemory = Integer.valueOf(line.split(": ")[1].replace("MB", "").trim());
                        }
                        if (line.trim().startsWith("Shared Memory:")) {
                            sharedMemory = Integer.valueOf(line.split(": ")[1].replace("MB", "").trim());
                        }

                        info += line.trim()+"\n";
                    }
                }
                LogMaster.important("GPU info: \n " + info);
                setMeasured(true);
                applyAdjustment();
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public static boolean isMeasured() {
        return measured;
    }

    public static void setMeasured(boolean measured) {
        GpuTester.measured = measured;
    }
}