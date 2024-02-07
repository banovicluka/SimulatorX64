import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception{



        Processor processor = new Processor();
        processor.registrujLabele();
        processor.inicijalizujRegistre();
        processor.interpretCode();
        processor.prikaziStanjeRegistara();
        processor.prikaziStanjeMemorijskihLokacija();

    }

}