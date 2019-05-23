package masir_beheshti;
import org.bson.Document;

import java.util.Comparator;

public class UserComperator implements Comparator<Document> {
    @Override
    public int compare(Document o1, Document o2) {
        Integer firstLevel1 = o1.getInteger("firstLevel");
        Integer secondLevel1 = o1.getInteger("secondLevel");
        Integer thirdLevel1 = o1.getInteger("thirdLevel");
        Integer finalLavel1 = o1.getInteger("finalLevel");
        Integer firstLevel2 = o2.getInteger("firstLevel");
        Integer secondLevel2 = o2.getInteger("secondLevel");
        Integer thirdLevel2 = o2.getInteger("thirdLevel");
        Integer finalLavel2 = o2.getInteger("finalLevel");
        firstLevel1 = firstLevel1.equals(-1) ? 0 : firstLevel1;
        firstLevel2 = firstLevel2.equals(-1) ? 0 : firstLevel2;
        secondLevel1 = secondLevel1.equals(-1) ? 0 : secondLevel1;
        secondLevel2 = secondLevel2.equals(-1) ? 0 : secondLevel2;
        thirdLevel1 = thirdLevel1.equals(-1) ? 0 : thirdLevel1;
        thirdLevel2 = thirdLevel2.equals(-1) ? 0 : thirdLevel2;
        finalLavel1 = finalLavel1.equals(-1) ? 0 : finalLavel1;
        finalLavel2 = finalLavel2.equals(-1) ? 0 : finalLavel2;
        return -(firstLevel1 + secondLevel1 + thirdLevel1 + finalLavel1 - firstLevel2 - secondLevel2 - thirdLevel2 - finalLavel2);
    }
}
