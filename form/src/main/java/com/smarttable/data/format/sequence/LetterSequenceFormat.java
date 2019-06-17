package com.smarttable.data.format.sequence;


import com.smarttable.utils.LetterUtils;


public class LetterSequenceFormat extends BaseSequenceFormat{

    @Override
    public String format(Integer position) {
        return LetterUtils.ToNumberSystem26(position);
    }
}
