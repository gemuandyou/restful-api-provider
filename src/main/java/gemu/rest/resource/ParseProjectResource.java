package gemu.rest.resource;

import gemu.rest.annotation.MyUrl;
import gemu.rest.app.ClassTree;
import gemu.rest.app.GetAllClassForProject;
import gemu.rest.core.ReturnType;

import java.io.FileNotFoundException;


@MyUrl("/project")
public class ParseProjectResource {

    GetAllClassForProject currObj = new GetAllClassForProject();

    @MyUrl(value = "/parseClassRlt", type = ReturnType.STRING)
    public String parseClassRlt() {
        try {
            ClassTree classTree = GetAllClassForProject.getClassTree();
            return classTree.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "{}";
    }

}
