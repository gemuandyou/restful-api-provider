package gemu.rest.resource;

import gemu.rest.annotation.MyUrl;
import gemu.rest.app.ClassTree;
import gemu.rest.app.GetAllClassForProject;
import gemu.rest.core.ReturnType;
import gemu.rest.log.JavaLog;
import gemu.rest.tool.auxiliary.JsonStringFormatParse;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


@MyUrl("/project")
public class ParseProjectResource {

    GetAllClassForProject currObj = new GetAllClassForProject();

    @MyUrl(value = "/parseClassRlt", type = ReturnType.STRING)
    public String parseClassRlt(HttpServletResponse response) {
        response.setCharacterEncoding("GBK");
        try {
            GetAllClassForProject.srcPath = "D:\\idea_workspace\\yunan_core\\web4Company";
            ClassTree classTree = GetAllClassForProject.getClassTree();
            File file = new File("D:/tree.json");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(JsonStringFormatParse.formatParse(classTree.toString()));
            fileWriter.flush();
            fileWriter.close();
            return classTree.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JavaLog.issue(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            JavaLog.issue(e.getMessage());
        }
        return "{}";
    }

}
