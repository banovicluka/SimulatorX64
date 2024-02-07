import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Processor {

    private static final Map<String, Long> registri = new HashMap<String, Long>();
    private static final Map<String, Long> memorijskeLokacije = new HashMap<String, Long>();
    private static final Map<String, Integer> labele = new HashMap<String, Integer>();
    private int komparator ;


    public void inicijalizujRegistre(){
        registri.put("EAX", (long)0);
        registri.put("EBX", (long)0);
        registri.put("ECX", (long)0);
        registri.put("EDX", (long)0);
    }

    public void prikaziStanjeRegistara(){
        System.out.print("REGISTRI: ");
        registri.entrySet().stream().forEach(x -> {
            System.out.print(x + "; ");
        });
        System.out.println();
    }

    public void prikaziStanjeMemorijskihLokacija(){
        System.out.print("MEMORIJSKE LOKACIJE: ");
        memorijskeLokacije.entrySet().stream().forEach(x -> {
            System.out.print(x + "; ");
        });
        System.out.println();
    }

    public void registrujLabele(){
        try {
            Path putanja = Paths.get("izvorniKod.txt");
            List<String> instrukcije = Files.readAllLines(putanja);
            for(int i = 0; i<instrukcije.size();i++){
                if(instrukcije.get(i).contains(":"))
                    labele.put(instrukcije.get(i).substring(0,instrukcije.get(i).length()-1), i);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public void printajLabele(){
        labele.entrySet().stream().forEach(System.out::println);
    }





    private static int brojTrenutneLinije = 0;


    public void interpretCode() throws Exception{
        try{
            String linija;
            String[] komanda = new String[10];
            String nazivKomande;
            String[] argumenti = new String[10];
            String prviArgument;
            String drugiArgument;
            Scanner skener = new Scanner(System.in);
            Path putanja = Paths.get("izvorniKod.txt");
            List<String> instrukcije = Files.readAllLines(putanja);
            for(brojTrenutneLinije = 0; brojTrenutneLinije<instrukcije.size(); brojTrenutneLinije++){
                linija = instrukcije.get(brojTrenutneLinije);
                if("BREAKPOINT_START".equals(linija)){
                    while(!"BREAKPOINT_END".equals(instrukcije.get(brojTrenutneLinije+1))){
                        linija = instrukcije.get(++brojTrenutneLinije);
                        prviArgument = null;
                        drugiArgument = null;
                        komanda = linija.split(" ");
                        nazivKomande = komanda[0];
                        prikaziStanjeRegistara();
                        prikaziStanjeMemorijskihLokacija();
                        while (skener.nextLine().length() != 0){};
                        argumenti = komanda[1].split(",");
                        prviArgument = argumenti[0];
                        if(argumenti.length>1)
                            drugiArgument = argumenti[1];
                        interpetirajLiniju(nazivKomande, prviArgument, drugiArgument);
                    }
                }
                if("BREAKPOINT_END".equals(instrukcije.get(brojTrenutneLinije))) {
                    brojTrenutneLinije++;
                }
                linija = instrukcije.get(brojTrenutneLinije);
                if(linija.contains(":"))
                    linija = instrukcije.get(++brojTrenutneLinije);
                prviArgument = null;
                drugiArgument = null;
                komanda = linija.split(" ");
                nazivKomande = komanda[0];
                argumenti = komanda[1].split(",");
                prviArgument = argumenti[0];
                if(argumenti.length>1)
                    drugiArgument = argumenti[1];
                interpetirajLiniju(nazivKomande, prviArgument, drugiArgument);
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }


    public void interpetirajLiniju(String nazivKomande, String prviArgument, String drugiArgument) throws Exception{
        switch(nazivKomande){
            case "ADD":
                if(prviArgument != null && drugiArgument != null){
                    if(prviArgument.startsWith("[") && prviArgument.endsWith("]") &&
                            registri.containsKey(prviArgument.substring(1, prviArgument.length()-1)))
                        prviArgument = "[" + registri.get(prviArgument.substring(1, prviArgument.length()-1)) + "]";
                    if(drugiArgument.startsWith("[") && drugiArgument.endsWith("]") &&
                            registri.containsKey(drugiArgument.substring(1, drugiArgument.length()-1)))
                        drugiArgument = "[" + registri.get(drugiArgument.substring(1, drugiArgument.length()-1)) + "]";
                    if(registri.containsKey(prviArgument)){
                        if(registri.containsKey(drugiArgument)){
                            registri.put(prviArgument, registri.get(prviArgument) + registri.get(drugiArgument));
                        }
                        else if(memorijskeLokacije.containsKey(drugiArgument)){
                            registri.put(prviArgument, registri.get(prviArgument) + memorijskeLokacije.get(drugiArgument));
                        }

                        else{
                            try{
                                Long konstanta = Long.parseLong(drugiArgument);
                                registri.put(prviArgument, registri.get(prviArgument) + konstanta);
                            }
                            catch(NumberFormatException e){
                                throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                            }
                        }
                    }
                    else if(memorijskeLokacije.containsKey(prviArgument)){
                        if(registri.containsKey(drugiArgument)){
                            memorijskeLokacije.put(prviArgument, memorijskeLokacije.get(prviArgument) + registri.get(drugiArgument));
                        }
                        else if(memorijskeLokacije.containsKey(drugiArgument)){
                            memorijskeLokacije.put(prviArgument, memorijskeLokacije.get(prviArgument) + memorijskeLokacije.get(drugiArgument));
                        }

                        else{
                            try{
                                Long konstanta = Long.parseLong(drugiArgument);
                                memorijskeLokacije.put(prviArgument, memorijskeLokacije.get(prviArgument) + konstanta);
                            }
                            catch(NumberFormatException e){
                                throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                            }
                        }
                    }
                    else
                    {
                        throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                    }
                }
                else{
                    throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                }
                break;
            case "SUB":
                if(prviArgument != null && drugiArgument != null){
                    if(prviArgument.startsWith("[") && prviArgument.endsWith("]") &&
                            registri.containsKey(prviArgument.substring(1, prviArgument.length()-1)))
                        prviArgument = "[" + registri.get(prviArgument.substring(1, prviArgument.length()-1)) + "]";
                    if(drugiArgument.startsWith("[") && drugiArgument.endsWith("]") &&
                            registri.containsKey(drugiArgument.substring(1, drugiArgument.length()-1)))
                        drugiArgument = "[" + registri.get(drugiArgument.substring(1, drugiArgument.length()-1)) + "]";
                    if(registri.containsKey(prviArgument)){
                        if(registri.containsKey(drugiArgument)){
                            registri.put(prviArgument, registri.get(prviArgument) - registri.get(drugiArgument));
                        }
                        else if(memorijskeLokacije.containsKey(drugiArgument)){
                            registri.put(prviArgument, registri.get(prviArgument) - memorijskeLokacije.get(drugiArgument));
                        }
                        else{
                            try{
                                Long konstanta = Long.parseLong(drugiArgument);
                                registri.put(prviArgument, registri.get(prviArgument) - konstanta);
                            }
                            catch(NumberFormatException e){
                                throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                            }
                        }
                    }
                    else if(memorijskeLokacije.containsKey(prviArgument)){
                        if(registri.containsKey(drugiArgument)){
                            memorijskeLokacije.put(prviArgument, memorijskeLokacije.get(prviArgument) - registri.get(drugiArgument));
                        }
                        else if(memorijskeLokacije.containsKey(drugiArgument)){
                            memorijskeLokacije.put(prviArgument, memorijskeLokacije.get(prviArgument) - memorijskeLokacije.get(drugiArgument));
                        }

                        else{
                            try{
                                Long konstanta = Long.parseLong(drugiArgument);
                                memorijskeLokacije.put(prviArgument, memorijskeLokacije.get(prviArgument) - konstanta);
                            }
                            catch(NumberFormatException e){
                                throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                            }
                        }
                    }
                    else
                    {
                        throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                    }
                }
                else{
                    throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                }
                break;
            case "AND":
                if(prviArgument != null && drugiArgument != null){
                    if(prviArgument.startsWith("[") && prviArgument.endsWith("]") &&
                            registri.containsKey(prviArgument.substring(1, prviArgument.length()-1)))
                        prviArgument = "[" + registri.get(prviArgument.substring(1, prviArgument.length()-1)) + "]";
                    if(drugiArgument.startsWith("[") && drugiArgument.endsWith("]") &&
                            registri.containsKey(drugiArgument.substring(1, drugiArgument.length()-1)))
                        drugiArgument = "[" + registri.get(drugiArgument.substring(1, drugiArgument.length()-1)) + "]";
                    if(registri.containsKey(prviArgument)){
                        if(registri.containsKey(drugiArgument)){
                            registri.put(prviArgument, registri.get(prviArgument) & registri.get(drugiArgument));
                        }
                        else if(memorijskeLokacije.containsKey(drugiArgument)){
                            registri.put(prviArgument, registri.get(prviArgument) & memorijskeLokacije.get(drugiArgument));
                        }
                        else{
                            try{
                                Long konstanta = Long.parseLong(drugiArgument);
                                registri.put(prviArgument, registri.get(prviArgument) & konstanta);
                            }
                            catch(NumberFormatException e){
                                throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                            }
                        }
                    }
                    else if(memorijskeLokacije.containsKey(prviArgument)){
                        if(registri.containsKey(drugiArgument)){
                            memorijskeLokacije.put(prviArgument, memorijskeLokacije.get(prviArgument) & registri.get(drugiArgument));
                        }
                        else if(memorijskeLokacije.containsKey(drugiArgument)){
                            memorijskeLokacije.put(prviArgument, memorijskeLokacije.get(prviArgument) & memorijskeLokacije.get(drugiArgument));
                        }

                        else{
                            try{
                                Long konstanta = Long.parseLong(drugiArgument);
                                memorijskeLokacije.put(prviArgument, memorijskeLokacije.get(prviArgument) & konstanta);
                            }
                            catch(NumberFormatException e){
                                throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                            }
                        }
                    }
                    else
                    {
                        throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                    }
                }
                else{
                    throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                }
                break;
            case "OR":
                if(prviArgument != null && drugiArgument != null){
                    if(prviArgument.startsWith("[") && prviArgument.endsWith("]") &&
                            registri.containsKey(prviArgument.substring(1, prviArgument.length()-1)))
                        prviArgument = "[" + registri.get(prviArgument.substring(1, prviArgument.length()-1)) + "]";
                    if(drugiArgument.startsWith("[") && drugiArgument.endsWith("]") &&
                            registri.containsKey(drugiArgument.substring(1, drugiArgument.length()-1)))
                        drugiArgument = "[" + registri.get(drugiArgument.substring(1, drugiArgument.length()-1)) + "]";
                    if(registri.containsKey(prviArgument)){
                        if(registri.containsKey(drugiArgument)){
                            registri.put(prviArgument, registri.get(prviArgument) | registri.get(drugiArgument));
                        }
                        else if(memorijskeLokacije.containsKey(drugiArgument)){
                            registri.put(prviArgument, registri.get(prviArgument) | memorijskeLokacije.get(drugiArgument));
                        }
                        else{
                            try{
                                Long konstanta = Long.parseLong(drugiArgument);
                                registri.put(prviArgument, registri.get(prviArgument) | konstanta);
                            }
                            catch(NumberFormatException e){
                                throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                            }
                        }
                    }
                    else
                    {
                        throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                    }
                }
                else if(memorijskeLokacije.containsKey(prviArgument)){
                    if(registri.containsKey(drugiArgument)){
                        memorijskeLokacije.put(prviArgument, memorijskeLokacije.get(prviArgument) | registri.get(drugiArgument));
                    }
                    else if(memorijskeLokacije.containsKey(drugiArgument)){
                        memorijskeLokacije.put(prviArgument, memorijskeLokacije.get(prviArgument) | memorijskeLokacije.get(drugiArgument));
                    }

                    else{
                        try{
                            Long konstanta = Long.parseLong(drugiArgument);
                            memorijskeLokacije.put(prviArgument, memorijskeLokacije.get(prviArgument) | konstanta);
                        }
                        catch(NumberFormatException e){
                            throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                        }
                    }
                }
                else{
                    throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                }
                break;
            case "NOT":
                if(prviArgument != null && drugiArgument == null){
                    if(prviArgument.startsWith("[") && prviArgument.endsWith("]") &&
                            registri.containsKey(prviArgument.substring(1, prviArgument.length()-1)))
                        prviArgument = "[" + registri.get(prviArgument.substring(1, prviArgument.length()-1)) + "]";
                    if(registri.containsKey(prviArgument)){
                        registri.put(prviArgument, ~registri.get(prviArgument));
                    }
                    else if(memorijskeLokacije.containsKey(prviArgument)){
                        memorijskeLokacije.put(prviArgument, ~memorijskeLokacije.get(prviArgument));
                    }
                    else{
                        throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                    }
                }
                else{
                    throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                }
                break;
            case "MOV":
                if(prviArgument != null && drugiArgument != null){
                    if(prviArgument.startsWith("[") && prviArgument.endsWith("]") &&
                        registri.containsKey(prviArgument.substring(1, prviArgument.length()-1)))
                        prviArgument = "[" + registri.get(prviArgument.substring(1, prviArgument.length()-1)) + "]";
                    if(drugiArgument.startsWith("[") && drugiArgument.endsWith("]") &&
                        registri.containsKey(drugiArgument.substring(1, drugiArgument.length()-1)))
                        drugiArgument = "[" + registri.get(drugiArgument.substring(1, drugiArgument.length()-1)) + "]";
                    if(!memorijskeLokacije.containsKey(drugiArgument) && drugiArgument.startsWith("[") && drugiArgument.endsWith("]")){
                        try{

                            Long adresa = Long.parseLong(drugiArgument.substring(1, drugiArgument.length()-1));
                            memorijskeLokacije.put(drugiArgument, (long)0);

                        }
                        catch (NumberFormatException e){
                            throw new Exception("Greska u liniji broj " + brojTrenutneLinije + ". Memorijska adresa mora biti broj");
                        }
                    }
                    if(!memorijskeLokacije.containsKey(prviArgument) && prviArgument.startsWith("[") && prviArgument.endsWith("]")){
                        try{
                            Long adresa = Long.parseLong(prviArgument.substring(1, prviArgument.length()-1));
                            memorijskeLokacije.put(prviArgument, (long)0);

                        }
                        catch (NumberFormatException e){
                            throw new Exception("Greska u liniji broj " + brojTrenutneLinije + ". Memorijska adresa mora biti broj");
                        }
                    }
                    if(registri.containsKey(prviArgument) && registri.containsKey(drugiArgument)){
                        registri.put(prviArgument, registri.get(drugiArgument));
                    }
                    else if(registri.containsKey(prviArgument) && memorijskeLokacije.containsKey(drugiArgument)){
                        registri.put(prviArgument, memorijskeLokacije.get(drugiArgument));
                    }
                    else if(memorijskeLokacije.containsKey(prviArgument) && registri.containsKey(drugiArgument)){
                        memorijskeLokacije.put(prviArgument, registri.get(drugiArgument));
                    }
                    else if(memorijskeLokacije.containsKey(prviArgument) && memorijskeLokacije.containsKey(drugiArgument)){
                        memorijskeLokacije.put(prviArgument, memorijskeLokacije.get(drugiArgument));
                    }
                    else if(registri.containsKey(prviArgument)){
                        try{
                            registri.put(prviArgument, Long.parseLong(drugiArgument));
                        }
                        catch(NumberFormatException e){
                            throw new Exception("Greska u liniji broj " + brojTrenutneLinije );
                        }
                    }
                    else if(memorijskeLokacije.containsKey(prviArgument)){
                        try{
                            memorijskeLokacije.put(prviArgument, Long.parseLong(drugiArgument));
                        }
                        catch(NumberFormatException e){
                            throw new Exception("Greska u liniji broj " + brojTrenutneLinije );
                        }
                    }
                    else{
                        throw new Exception("Greska u liniji broj " + brojTrenutneLinije );
                    }

                }
                else{
                    throw new Exception("Greska u liniji broj " + brojTrenutneLinije );
                }
                break;
            case "IN":
                Scanner skener = new Scanner(System.in);
                if(prviArgument != null && drugiArgument == null){
                    if(prviArgument.startsWith("[") && prviArgument.endsWith("]") &&
                            registri.containsKey(prviArgument.substring(1, prviArgument.length()-1)))
                        prviArgument = "[" + registri.get(prviArgument.substring(1, prviArgument.length()-1)) + "]";
                    if(registri.containsKey(prviArgument)){
                        try {
                            Long ulaz = skener.nextLong();
                            registri.put(prviArgument, ulaz);
                        }
                        catch(Exception e){
                            throw new Exception("Greska u linij broj " + brojTrenutneLinije + ". Moguc je unos samo brojcanih vrijednosti.");
                        }
                    }
                    else if(memorijskeLokacije.containsKey(prviArgument)){
                        try {
                            Long ulaz = skener.nextLong();
                            memorijskeLokacije.put(prviArgument, ulaz);
                        }
                        catch(Exception e){
                            throw new Exception("Greska u linij broj " + brojTrenutneLinije + ". Moguc je unos samo brojcanih vrijednosti.");
                        }
                    }
                    else if(!memorijskeLokacije.containsKey(prviArgument) && prviArgument.startsWith("[") && prviArgument.endsWith("]")){
                        try {
                            Long ulaz = skener.nextLong();
                            memorijskeLokacije.put(prviArgument, ulaz);
                        }
                        catch(Exception e){
                            throw new Exception("Greska u linij broj " + brojTrenutneLinije + ". Moguc je unos samo brojcanih vrijednosti.");
                        }
                    }
                    else{
                        throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                    }
                }
                else{
                    throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                }
                break;
            case "OUT":
                if(prviArgument != null && drugiArgument == null){
                    if(prviArgument.startsWith("[") && prviArgument.endsWith("]") &&
                            registri.containsKey(prviArgument.substring(1, prviArgument.length()-1)))
                        prviArgument = "[" + registri.get(prviArgument.substring(1, prviArgument.length()-1)) + "]";
                    if(registri.containsKey(prviArgument)){
                        System.out.println(registri.get(prviArgument));
                    }
                    else if(memorijskeLokacije.containsKey(prviArgument)){
                        System.out.println(memorijskeLokacije.get(prviArgument));
                    }
                    else{
                        throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                    }
                }
                else{
                    throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                }
                break;
            case "CMP":
                if(prviArgument != null && drugiArgument != null){
                    if(prviArgument.startsWith("[") && prviArgument.endsWith("]") &&
                            registri.containsKey(prviArgument.substring(1, prviArgument.length()-1)))
                        prviArgument = "[" + registri.get(prviArgument.substring(1, prviArgument.length()-1)) + "]";
                    if(drugiArgument.startsWith("[") && drugiArgument.endsWith("]") &&
                            registri.containsKey(drugiArgument.substring(1, drugiArgument.length()-1)))
                        drugiArgument = "[" + registri.get(drugiArgument.substring(1, drugiArgument.length()-1)) + "]";
                    if(registri.containsKey(prviArgument)){
                        if(registri.containsKey(drugiArgument)){
                            komparator = registri.get(prviArgument).compareTo(registri.get(drugiArgument));
                        }
                        else if(memorijskeLokacije.containsKey(drugiArgument)){
                            komparator = registri.get(prviArgument).compareTo(memorijskeLokacije.get(drugiArgument));
                        }
                        else{
                            try{
                                Long konstanta = Long.parseLong(drugiArgument);
                                komparator = registri.get(prviArgument).compareTo(konstanta);
                            }
                            catch(NumberFormatException e){
                                throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                            }
                        }
                    }
                    else if(memorijskeLokacije.containsKey(prviArgument)){
                        if(registri.containsKey(drugiArgument)){
                            komparator = memorijskeLokacije.get(prviArgument).compareTo(registri.get(drugiArgument));
                        }
                        else if(memorijskeLokacije.containsKey(drugiArgument)){
                            komparator = memorijskeLokacije.get(prviArgument).compareTo(memorijskeLokacije.get(drugiArgument));
                        }

                        else{
                            try{
                                Long konstanta = Long.parseLong(drugiArgument);
                                komparator = memorijskeLokacije.get(prviArgument).compareTo(konstanta);
                            }
                            catch(NumberFormatException e){
                                throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                            }
                        }
                    }
                    else
                    {
                        throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                    }
                }
                else{
                    throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                }
                break;

            case "JE":
                if(prviArgument != null && drugiArgument == null){
                    if(komparator == 0)
                        brojTrenutneLinije=labele.get(prviArgument);
                }
                else{
                    throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                }
                break;

            case "JNE":
                if(prviArgument != null && drugiArgument == null){
                    if(komparator > 0 || komparator < 0)
                        brojTrenutneLinije=labele.get(prviArgument);
                }
                else{
                    throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                }
                break;
            case "JGE":
                if(prviArgument != null && drugiArgument == null){
                    if(komparator > 0)
                        brojTrenutneLinije=labele.get(prviArgument);
                }
                else{
                    throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                }
                break;
            case "JL":
                if(prviArgument != null && drugiArgument == null){
                    if(komparator < 0)
                        brojTrenutneLinije=labele.get(prviArgument);
                }
                else{
                    throw new Exception("Greska u liniji broj " + brojTrenutneLinije);
                }
                break;

        }
    }
}
