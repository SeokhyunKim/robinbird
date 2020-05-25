package org.robinbird.presentation;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.robinbird.util.Msgs;

public class PlantUMLPresentationTest {

    @Test
    public void test_presentClusteringNodes() {
        String bla = "@startuml\n"
                             + "[asdf, wefew,  sadds]\n"
                             + "[sdfwew]\n"
                             + "[brgrwgwgwg]\n"
                             + "@enduml";
        List<Set<String>> hoho = parseForTest(bla);
    }

    private List<Set<String>> parseForTest(@NonNull final String script) {
        char[] chars = script.toCharArray();
        int ptr = 0;
        int state = 0;
        List<Set<String>> nameSets = new ArrayList<>();
        while (ptr < chars.length) {
            if (chars[ptr++] == '@') {
                if (state == 0) {
                    if (isEqual(chars, ptr, 8, "startuml".toCharArray())) {
                        state = 1;
                        ptr += 8;
                    }
                } else if (state == 1) {
                    if (isEqual(chars, ptr, 6, "enduml".toCharArray())) {
                        return nameSets;
                    }
                } else {
                    throw new IllegalArgumentException("Wrong char: @");
                }
            } else if (chars[ptr++] == '[') {
                int closingPtr = ptr;
                while (closingPtr < chars.length && chars[closingPtr] != ']') {
                    ++closingPtr;
                }
                if (closingPtr >= chars.length) {
                    throw new IllegalArgumentException("Missing ]");
                }
                String[] names =
                        StringUtils.split(new String(chars, ptr, closingPtr - ptr),
                                          ", ");
                nameSets.add(Sets.newHashSet(names));
                ptr = closingPtr + 1;
            } else {
                ++ptr;
            }
        }
        return nameSets;
    }

    private boolean isEqual(char[] chars, int start, int length, char[] compared) {
        int i = start;
        int j = 0;
        while (length-- > 0) {
            if (chars[i++] != compared[j++]) {
                return false;
            }
        }
        return true;
    }
}
