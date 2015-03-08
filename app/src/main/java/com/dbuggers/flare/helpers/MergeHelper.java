package com.dbuggers.flare.helpers;

import com.dbuggers.flare.models.MessageEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MergeHelper {


    public static List<MessageEntry> merge(List<MessageEntry> firstList, List<MessageEntry> secondList){

        if(firstList == null || secondList == null){
            return null;
        }

        Set<MessageEntry> output = new HashSet<MessageEntry>();

        output.addAll(secondList);
        output.addAll(firstList);

        return asSortedList(output);

    }

    private static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }


}
