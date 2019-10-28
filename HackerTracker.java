//package hack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class HackerTracker {

    public HackerTracker() throws IOException {


    }


     /**    CREDITS TO : https://www.rgagnon.com/javadetails/java-0624.html
     * @param  target  hour to check
     * @param  start   interval start
     * @param  end     interval end
     * @return true    true if the given hour is between
     */
    public static boolean isHourInInterval(String target, String start, String end) {
        return ((target.compareTo(start) >= 0)
                && (target.compareTo(end) <= 0));
                /*                                                                  Histoire de m'assurer que je comprends de quoi je me sers :
                https://docs.oracle.com/javase/7/docs/api/java/lang/String.html " -> compareTo(String anotherString) : Compares two strings lexicographically. "
                                                                              et  -> https://en.wikipedia.org/wiki/Lexicographical_order */
    }


    public static void main(String[] args) throws IOException {

         /*
            1) Parcourez le fichier pour trouver la liste de tous les utilisateurs qui se sont connectés, 
            enregistrez cette liste dans un fichier utilisateurs.txt.
        */

        // potentially useless. I might remove it later.    EDIT : I believe I could do without it with something like -> writer1.write(readLine.split(";")[1] + "\n");
                                                        // But the code works and it is easier to understand to me so I'll let it this way for the time being.
        ArrayList<String> usersList = new ArrayList<String>();

        // we create a file Object based on an actual file existing on the given path.
        File f = new File("connexion.log");

        // we create a reader tool that is targetting the file Object we just created.
        BufferedReader b = new BufferedReader(new FileReader(f));

        // we create a String, for now we initiate it as empty while not NULL
        String readLine = "";

        
        // as long as the String readline, to which we give the value obtained by calling the method readLine of b, is not NULL;
        // (note that this way, we actually change the next value that will be given to readLine by the following call, while checking the condition)
        while ((readLine = b.readLine()) != null) {
    
            // a single line from connexion.log is built like this : 138.129.198.221;cursory;06/07/16 16:59
            // we split( ";" ) and take the index 1.
            usersList.add(readLine.split(";")[1]);
        }

        File users = new File("utilisateurs.txt");

        BufferedWriter writer1 = new BufferedWriter(new FileWriter(users));

        for (String str : usersList) {  
                writer1.write(str + "\n");
        }


        /*
        2) On soupçonne qu’une personne se connecte en dehors des heures d’ouverture des bureaux (8h-19h), peut-être depuis un poste distant. 
        Utilisez un script pour retrouver l’identifiant de cette personne ainsi que l’ip à la laquelle elle se connectait
        */
        String suspectID;
        String suspectIP;
    
        // on récupère / réinitialise nos outils
        b = new BufferedReader(new FileReader(f));
        readLine = "";

        while ((readLine = b.readLine()) != null) {
            // a single line from connexion.log is built like this : 138.129.198.221;cursory;06/07/16 16:59   
            if(!isHourInInterval(readLine.split(" ")[1], "08:00", "19:00")){ 
                suspectID=readLine.split(";")[1];
                suspectIP=readLine.split(";")[0];
                // d'après l'énoncé il n'y en a qu'un à trouver. Donc en principe ou devrait pouvoir s'en tenir aux lignes ci-dessus.
                // par acquit de conscience je vais quand même glisser un println pour m'assurer qu'on ne voit pas plusieurs noms différents apparaitre (et écraser le précédent).

                System.out.println("We found a suspect ! \nID : " + suspectID + "\nIP : " + suspectIP); 
            }
        } 



        /*
       3) Le service de sécurité informatique a fournit un fichier contenant les ips dangereuses : warning.txt. Lisez ce fichier pour construire une liste contenant toutes les ip dangereuses. 
       A l’aide de cette liste, relevez dans le fichier connexion.log tous les utilisateurs qui se sont connectés sur une de ces ip, on produira un fichier suspect.txt avec une ligne par 
       utilisateur et le nombre de fois qu’il s’est connecté à une ip interdite :

            josselin;17
            philippe;27
        */
        List <String> HazardousIP = new ArrayList<String>();
        HashMap<String, List<String>> usersVisitedIP = new HashMap<String, List<String>>();

        // on récupère / réinitialise nos outils
        f = new File("warning.txt");
        b = new BufferedReader(new FileReader(f));
        readLine = "";

        while ((readLine = b.readLine()) != null) {  
            HazardousIP.add(readLine);
        }                                                    

        f = new File("connexion.log");
        b = new BufferedReader(new FileReader(f));
        readLine = "";                                      // j'ai tellement réutilisé ces trois lignes j'aurai peut être du en faire une fonction qui aurait pris un String 
                                                            //  en argument (nom du fichier) et aurai renvoyé un BufferedReader. Et aurait reset readLine dans la foulée. Ah, tant pis.
        

       
        // on contruit la Hashmap dont il est question dans l'indice, qui répond aux critéres suivants :  en key, un identifiant. En value, une liste d'IP dangereuses.
        while ((readLine = b.readLine()) != null) {                                     // pour chaque log                                        
            for (String str : HazardousIP) {                                                // on le compare à chaque IP dangeureuse.
                if(str.equals(readLine.split(";")[0])){                                              // si on est face à un log concernant une IP dangereuse                     
                    if(!usersVisitedIP.containsKey(readLine.split(";")[1])){                    // si la key n'existe pas encore (si c'est la première fois que cet utilisateur est associé à une IP dangereuse)
                        usersVisitedIP.put(readLine.split(";")[1], new ArrayList<String>());    // on l'ajoute, avec une nouvelle liste comme value associée
                        usersVisitedIP.get(readLine.split(";")[1]).add(str);                    // et on ajoute cette IP à ladite liste 
                    }else{                                                                      // si la key y est déjà
                        usersVisitedIP.get(readLine.split(";")[1]).add(str);                    // on se contente d'ajouter l'IP la liste (value) correspondant à cette clé.
                    }
                }
            }
        }

        System.out.println(usersVisitedIP); // on a bien réussi à produire exactement ce qu'on voulait.

        // on a toutes les infos dont on a besoin pour produire suspect.txt. Les key sont les utilisateurs, et la taille de la liste en value est le nombre de connections.

        File suspects = new File("suspects.txt");

        writer1 = new BufferedWriter(new FileWriter(suspects));

        writer1.write("test");


        for (String key : usersVisitedIP.keySet()) {
            writer1.write(key + ";" + usersVisitedIP.get(key).size());
        }

    
    }     
}