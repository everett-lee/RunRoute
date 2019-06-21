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

        boolean[] opts = {true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true};
        double[] coords = {51.25, -1};

        ElementRepo repo = ctx.getBean(ElementRepo.class);
        GraphBuilder gb = ctx.getBean(GraphBuilder.class);
        gb.buildGraph(coords, 21, opts);

        System.out.println(repo.getWayRepo().size());
        System.out.println(repo.getOriginWay().getName());
        System.out.println(repo.getOriginWay().getNodeContainer().getStartNode());
        System.out.println(repo.getOriginWay().getNodeContainer().getEndNode());

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
