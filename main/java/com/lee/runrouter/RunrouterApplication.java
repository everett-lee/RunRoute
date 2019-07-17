package com.lee.runrouter;

import com.lee.runrouter.api.ResponseGeneratorController;
import com.lee.runrouter.executor.Executor;
import com.lee.runrouter.executor.ExecutorMain;
import com.lee.runrouter.graph.elementrepo.ElementRepo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

@SpringBootApplication
public class RunrouterApplication {

	public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(RunrouterApplication.class, args);

        boolean[] opts = {true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true};
        double[] coords = {51.889757, 0.900319};

        final long startTime = System.currentTimeMillis();
        ElementRepo repo = ctx.getBean(ElementRepo.class);
        Executor executor = ctx.getBean(ExecutorMain.class);


        final long endTime = System.currentTimeMillis();

        System.out.println(repo.getWayRepo().size());

        ResponseGeneratorController gc = ctx.getBean(ResponseGeneratorController.class);

        System.out.println("Total execution time: " + (endTime - startTime));


    }
	static void serialize(ElementRepo repo) {
        try {
            System.out.println("Starting... ");
            FileOutputStream fileOut =
                    new FileOutputStream("/home/lee/project/app/runrouter/src/repo.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(repo);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in /tmp/repo.ser");
        } catch (IOException i) {
            i.printStackTrace();
        }

    }

}
