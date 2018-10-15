package com.company.scrumit.web.task;

import com.company.scrumit.entity.Task;
import com.company.scrumit.web.entity.UiEvent;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.components.TextField;

import javax.inject.Named;
import java.util.Date;
import java.util.Map;

public class TaskEdit extends AbstractEditor<Task> {
    private static final long ONEDAY = 24*60*60*1000;


    @Named("fieldGroup.deadline")
    private DateField deadlineField;
    @Named("fieldGroup.begin")
    private DateField beginField;
    @Named("fieldGroup.duration")
    private TextField durationField;

    private Map<String, Object> params;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        durationField.addValueChangeListener(this::calcDates);
        beginField.addValueChangeListener(this::calcDates);
        deadlineField.addValueChangeListener(e -> {
            if(beginField.getValue()==null)
                return;
            durationField.setValue((deadlineField.getValue().getTime()-beginField.getValue().getTime())/ONEDAY);
        });
        this.params = params;
    }

    @Override
    protected void postInit() {
        super.postInit();
        setupPerfomer(params);
    }

    /**
     * setup Perfomer from tracker if exist
     *
     * @param params
     */
    private void setupPerfomer(Map<String, Object> params) {
        if (params.containsKey("perfomer")) {
            ((PickerField) getComponent("editBox.fieldGroupBox.fieldGroup.performer")).setValue(params.get("perfomer"));
        }
    }

    private void calcDates(ValueChangeEvent e) {
        if (beginField.getValue() == null || durationField.getValue() == null)
            return;
        Date d = beginField.getValue();
        d.setTime((d.getTime() + ONEDAY * Double.valueOf(durationField.getRawValue()).longValue()));
        deadlineField.setValue(d);
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        UiEvent.push("taskRefresh");
        return super.postCommit(committed, close);
    }
}