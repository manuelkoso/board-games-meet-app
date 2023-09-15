package it.units.boardgamesmeetapp.viewmodels.history;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.units.boardgamesmeetapp.models.Event;

public class HistoryViewModel extends ViewModel {

    private final MutableLiveData<Event> eventShown = new MutableLiveData<>();

    public MutableLiveData<Event> getEventShown() {
        return eventShown;
    }

    public void setEventShown(Event event) {
        eventShown.setValue(event);
    }

}
