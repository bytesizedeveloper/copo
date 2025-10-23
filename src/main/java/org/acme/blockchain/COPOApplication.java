package org.acme.blockchain;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.logging.Logger;

@QuarkusMain
public class COPOApplication implements QuarkusApplication {

    private static final String COPO = """
          
          _____                   _______                   _____                   _______         
         /\\    \\                 /::\\    \\                 /\\    \\                 /::\\    \\        
        /::\\    \\               /::::\\    \\               /::\\    \\               /::::\\    \\       
       /::::\\    \\             /::::::\\    \\             /::::\\    \\             /::::::\\    \\      
      /::::::\\    \\           /::::::::\\    \\           /::::::\\    \\           /::::::::\\    \\     
     /:::/\\:::\\    \\         /:::/~~\\:::\\    \\         /:::/\\:::\\    \\         /:::/~~\\:::\\    \\    
    /:::/  \\:::\\    \\       /:::/    \\:::\\    \\       /:::/__\\:::\\    \\       /:::/    \\:::\\    \\   
   /:::/    \\:::\\    \\     /:::/    / \\:::\\    \\     /::::\\   \\:::\\    \\     /:::/    / \\:::\\    \\  
  /:::/    / \\:::\\    \\   /:::/____/   \\:::\\____\\   /::::::\\   \\:::\\    \\   /:::/____/   \\:::\\____\\ 
 /:::/    /   \\:::\\    \\ |:::|    |     |:::|    | /:::/\\:::\\   \\:::\\____\\ |:::|    |     |:::|    |
/:::/____/     \\:::\\____\\|:::|____|     |:::|    |/:::/  \\:::\\   \\:::|    ||:::|____|     |:::|    |
\\:::\\    \\      \\::/    / \\:::\\    \\   /:::/    / \\::/    \\:::\\  /:::|____| \\:::\\    \\   /:::/    / 
 \\:::\\    \\      \\/____/   \\:::\\    \\ /:::/    /   \\/_____/\\:::\\/:::/    /   \\:::\\    \\ /:::/    /  
  \\:::\\    \\                \\:::\\    /:::/    /             \\::::::/    /     \\:::\\    /:::/    /   
   \\:::\\    \\                \\:::\\__/:::/    /               \\::::/    /       \\:::\\__/:::/    /    
    \\:::\\    \\                \\::::::::/    /                 \\::/____/         \\::::::::/    /     
     \\:::\\    \\                \\::::::/    /                   ~~                \\::::::/    /      
      \\:::\\    \\                \\::::/    /                                       \\::::/    /       
       \\:::\\____\\                \\::/____/                                         \\::/____/        
        \\::/    /                 ~~                                                ~~              
         \\/____/                                                                                                                                                                           
""";

    private final Logger logger = Logger.getLogger(COPOApplication.class.getName());

    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    private String datasourceUrl;

    public static void main(String... args) {
        Quarkus.run(COPOApplication.class, args);
    }

    @Override
    public int run(String... args) {
        logger.info(COPO);
        logger.info("COPO Blockchain Application started successfully.");
        logger.info("Connected to database at: " + datasourceUrl);
        logger.info("Swagger UI available at: http://localhost:8080/q/swagger-ui/");
        logger.info("OpenAPI specification available at: http://localhost:8080/q/openapi");

        Quarkus.waitForExit();
        return 0;
    }
}
