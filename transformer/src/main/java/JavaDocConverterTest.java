import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.networkedassets.autodoc.transformer.handleRepoPush.Code;
import com.networkedassets.autodoc.transformer.handleRepoPush.Documentation;
import com.networkedassets.autodoc.transformer.handleRepoPush.core.JavadocGenerator;

public class JavaDocConverterTest {

	public static void main(String[] args) throws IOException {

		Code code = new Code(Paths.get("C:\\NA\\XML\\javadoc"));
		JavadocGenerator gen = new JavadocGenerator();
		Documentation doc = gen.generateFrom(code);

		Files.write(Paths.get("C:\\NA\\XML\\javadoc_main.txt"), doc.getPieces().get(0).getContent().getBytes());
	}

}

