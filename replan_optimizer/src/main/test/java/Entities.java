package java;

import com.esotericsoftware.yamlbeans.YamlReader;
import entities.Employee;
import entities.Feature;
import entities.Skill;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by kredes on 10/05/2017.
 */
public class Entities {

    public static void fromFile(
            File f,
            List<Skill> skills, List<Employee> employees, List<Feature> features) throws IOException
    {
        YamlReader reader = new YamlReader(new FileReader(f));
        Object object = reader.read();
        System.out.println(object);
        List list = (List) object;
        System.out.println(list);
    }

}
