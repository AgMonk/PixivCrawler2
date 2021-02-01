package com.gin.pixivcrawler.utils.ngaUtils.bbsCode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * table
 *
 * @author bx002
 * @date 2021/1/16 15:56
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
public class Table extends NgaBbsTag {
    List<Tr> trList = new ArrayList<>();
    String prefix = "\n";

    public Table(String... ths) {
        addTr(ths);
    }

    public void addTr(String... tds) {
        Tr tr = new Tr();
        for (String td : tds) {
            tr.addText(new Td(td).toString());
        }
        trList.add(tr);
    }

    @Override
    public String getText() {
        return trList.stream().map(Tr::toString).collect(Collectors.joining()) + "\n";
    }

}
