package com.zhaohg.route;

import org.apache.camel.main.Main;

/**
 * A Camel Application
 */
public class MainApp {

    /**
     * A main() so we can easily run these routing rules in our IDE
     * 一个main()就可以很容易地运行这些路由规则在我们的IDE
     */
    public static void main(String... args) throws Exception {
        Main main = new Main();
        main.addRouteBuilder(new MyRouteBuilder());
        main.run(args);
    }

//    <!-- https://mvnrepository.com/artifact/org.apache.camel/camel-core -->
//    <dependency>
//    <groupId>org.apache.camel</groupId>
//    <artifactId>camel-core</artifactId>
//    <version>2.18.0</version>
//    </dependency>

}

