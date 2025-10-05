import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

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

    public static void main(String... args) {
        Quarkus.run(COPOApplication.class, args);
    }

    @Override
    public int run(String... args) {
        logger.info(COPO);
        logger.info("COPO Blockchain Application started successfully.");
        logger.info("Swagger UI available at: http://localhost:8080/q/swagger-ui/");
        logger.info("OpenAPI specification available at: http://localhost:8080/q/openapi");

        Quarkus.waitForExit();
        return 0;
    }
}
