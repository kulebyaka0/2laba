import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


class CSVObject{
    public String city;
    public String street;
    public Integer house;
    public Integer floor;
    CSVObject(String[] arr) {
        city = arr[0];
        street = arr[1];
        house = Integer.parseInt(arr[2]);
        floor = Integer.parseInt(arr[3]);
    }
    CSVObject(Element eElement){
        city = eElement.getAttribute("city");
        street = eElement.getAttribute("street");
        house = Integer.parseInt(eElement.getAttribute("house"));
        floor = Integer.parseInt(eElement.getAttribute("floor"));
    }

    public String toStringCSVObject(){
        String tempStr;
        tempStr=city+" "+street+" "+house.toString()+" "+floor.toString();
        return tempStr;
    }
    public boolean equals(CSVObject o)
    {
        if (this==o){
            return true;
        } else if (!Objects.equals(this.city, o.city)) {
            return false;
        }else if (!Objects.equals(this.street, o.street)) {
            return false;
        }else if (!Objects.equals(this.floor, o.floor)) {
            return false;
        }else if (!Objects.equals(this.house, o.house)) {
            return false;
        }else{
            return true;
        }

    }
}

class CityInfo {
    public Integer onefloor;
    public Integer twofloor;
    public Integer threefloor;
    public Integer fourfloor;
    public Integer fivefloor;
    CityInfo(){
        onefloor=0;
        twofloor=0;
        threefloor=0;
        fourfloor=0;
        fivefloor=0;
    }
    void Append(Integer i){
        if(i==1){
            this.onefloor++;
        } else if (i==2) {
            this.twofloor++;
        } else if (i==3) {
            this.threefloor++;
        }else if(i==4){
            this.fourfloor++;
        } else if (i==5) {
            this.fivefloor++;
        }
    }
    public String toString(){
        String temp= " 1 Этаж:" + onefloor+" 2 Этажа:" + twofloor+" 3 Этажа:" + threefloor+" 4 Этажа:" + fourfloor+" 5 Этажей:" + fivefloor;
        return temp;
    }


}


class DataSet{
    HashMap<String, CSVObject> UniqueValues;
    HashMap<String, Integer> Duplicates;
    HashMap<String, CityInfo> CityInformation;

    DataSet(String link) throws IOException, ParserConfigurationException, SAXException {
        long startTime = System.currentTimeMillis();
        CityInformation=null;
        String cheklink;
        if (link.length()>4) {
            cheklink = link.substring(link.length() - 4);
        }else{
            cheklink="error";
        }
        if(cheklink.equals(".csv")){
            System.out.println("Файл типа csv");
            FileReader filereader = new FileReader(link);
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).withCSVParser(parser).build();
            String[] nextRecord;
            String UniqueValuesKey;

            this.UniqueValues = new HashMap<>();
            this.Duplicates = new HashMap<>();

            while ((nextRecord=csvReader.readNext())!=null) {
                CSVObject temp = new CSVObject(nextRecord);
                UniqueValuesKey = temp.toStringCSVObject();
                if (!UniqueValues.containsKey(UniqueValuesKey)) {
                    UniqueValues.put(UniqueValuesKey, temp);
                } else {
                    if (Duplicates.containsKey(UniqueValuesKey)) {
                        Duplicates.put(UniqueValuesKey, Duplicates.get(UniqueValuesKey) + 1);
                    } else {
                        Duplicates.put(UniqueValuesKey, 1);
                    }
                }
            }
        }else if(cheklink.equals(".xml")){
            System.out.println("Файл типа xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(link));
            document.getDocumentElement().normalize();

            Element root = document.getDocumentElement();
            NodeList nList = document.getElementsByTagName("item");

            this.UniqueValues = new HashMap<>();
            this.Duplicates = new HashMap<>();
            String UniqueValuesKey;


            for (int num = 0; num < nList.getLength(); num++) {
                Node node = nList.item(num);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    CSVObject temp=new CSVObject(eElement);
                    UniqueValuesKey=temp.toStringCSVObject();
                    if(!UniqueValues.containsKey(UniqueValuesKey)) {
                        UniqueValues.put(UniqueValuesKey, temp);
                    }else {
                        if(Duplicates.containsKey(UniqueValuesKey)){
                            Duplicates.put(UniqueValuesKey,Duplicates.get(UniqueValuesKey)+1);
                        }else {
                            Duplicates.put(UniqueValuesKey, 1);
                        }
                    }


                }
            }
        }
        if((cheklink.equals(".xml")||(cheklink.equals(".csv")))) {
            this.CityInformation = new HashMap<>();
            for (String temp : this.UniqueValues.keySet()) {
                if (this.CityInformation.containsKey(UniqueValues.get(temp).city)) {
                    this.CityInformation.get(this.UniqueValues.get(temp).city).Append(this.UniqueValues.get(temp).floor);
                } else {
                    this.CityInformation.put(this.UniqueValues.get(temp).city, new CityInfo());
                    this.CityInformation.get(this.UniqueValues.get(temp).city).Append(this.UniqueValues.get(temp).floor);
                }
            }
            long endTime = System.currentTimeMillis();
            long elapsedTime = (endTime - startTime);
            System.out.println("Время выполнения: "+elapsedTime);
        }else {
            System.out.println("не верная ссылка");
        }
    }


    void print(){
        if((UniqueValues !=null)&&(Duplicates!=null)) {
            System.out.println("Дубликаты: ");
            for (String temp : Duplicates.keySet()) {
                System.out.println(temp + " Встречается:" + Duplicates.get(temp).toString());
            }
            if (CityInformation != null) {
                System.out.println("Информаиця об этажах:");
                for (String temp : CityInformation.keySet()) {
                    System.out.println(temp + ": " + CityInformation.get(temp).toString());
                }
            }
        }
    }
}
public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        String link = "";
        while (true) {
            Scanner input = new Scanner(System.in);
            System.out.print("Пожалуйста, введите путь к файлу или exit для выхода: ");
            link = input.nextLine();
            if(!link.equals("exit")){
                DataSet test = new DataSet(link);
                test.print();
            }else{break;}
        }
    }
}