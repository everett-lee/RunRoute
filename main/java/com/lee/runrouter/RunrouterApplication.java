package com.lee.runrouter;

import com.lee.runrouter.executor.Executor;
import com.lee.runrouter.executor.ExecutorMain;
import com.lee.runrouter.graph.elementrepo.ElementRepo;

import com.lee.runrouter.graph.graphbuilder.GraphBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

@SpringBootApplication
public class RunrouterApplication {
    static ApplicationContext ctx;

	public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(RunrouterApplication.class, args);

        boolean[] opts = {true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true};
        double[] coords = {51.446529, -0.125446};

        ElementRepo repo = ctx.getBean(ElementRepo.class);
        Executor executor = ctx.getBean(ExecutorMain.class);
        GraphBuilder gb = ctx.getBean(GraphBuilder.class);
    //
    //        gb.buildGraph(coords, 25000, opts);
    //        serialize(repo);

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

    public static void refreshBeans() {
        ((ConfigurableApplicationContext) ctx).refresh();
    }

}
