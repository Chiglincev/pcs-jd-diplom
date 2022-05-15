import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        int port = 8989;

        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
//        System.out.println(engine.search("бизнес"));

        Server server = new Server(port);
        server.start(engine);
    }
}