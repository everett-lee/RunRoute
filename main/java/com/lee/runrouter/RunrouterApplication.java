package com.lee.runrouter;

import com.lee.runrouter.graph.elementrepo.ElementRepo;
import com.lee.runrouter.graph.graphbuilder.GraphBuilder;
import com.lee.runrouter.graph.graphbuilder.graphelement.Way;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class RunrouterApplication {

	public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(RunrouterApplication.class, args);

        boolean[] opts = {true, true, true, true, true, false, false,
                true, true, true, true, true, true, true, true};
        double[] coords = {51.916036,1.042514};

        ElementRepo repo = ctx.getBean(ElementRepo.class);
        GraphBuilder gb = ctx.getBean(GraphBuilder.class);
        gb.buildGraph(coords, 3.5, opts);

        travel(repo.getWayRepo().get(1), repo);

	}

	static void travel(Way way, ElementRepo repo) {
	    double distance = 0;
        List<Way> vistied = new ArrayList<>();

	    while (distance < 20000) {
            exit:
	        while(true) {
	            vistied.add(way);
	            for (Node n: way.getNodeContainer().getNodes()) {
	                for (Way w: repo.getNodeToWay().get(n.getId())) {
	                    if (w != way && !(vistied.contains(w))) {
	                        distance += way.getLength();
                            System.out.println(way.getName());
                            System.out.println(distance);
                            way = w;
	                        break exit;
                        }
                    }
                }
            }
        }
    }

}
