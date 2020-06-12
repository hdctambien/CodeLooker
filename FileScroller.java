import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;

public class FileScroller<E> implements ListSelectionListener
{
  private JScrollPane scrollbox;
  private JList<E> combobox;
  private DefaultListModel<E> listModel;
  private ActionListener listener;
  private String id;

  private int populating;

  public FileScroller(String id)
  {
    this.id = id;
    this.populating = 0;
    listModel = new DefaultListModel<>();
    combobox = new JList<>(listModel);
    combobox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    combobox.setLayoutOrientation(JList.VERTICAL);
    scrollbox = new JScrollPane(combobox);

    combobox.getSelectionModel().addListSelectionListener(this);

    combobox.setFocusable(false);
    scrollbox.setFocusable(false);
  }

  public void addActionListener(ActionListener listener)
  {
    this.listener = listener;
  }

  public void valueChanged(ListSelectionEvent e)
  {
    if(null == listener) return;
    if(populating > 0) return;
    if(e.getValueIsAdjusting()) return;
    listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, id));
  }

  public Component getComponent()
  {
    return scrollbox;
  }

  public int getSize()
  {
    return listModel.getSize();
  }

  public Object[] getElements()
  {
    return listModel.toArray();
  }

  public void clear()
  {
    populating++;
    listModel.removeAllElements();
    populating--;
  }

  public void populate(E[] data)
  {
    populating++;
    //remove all elements from combobox
    clear();

    for(E elem : data)
    {
      listModel.addElement(elem);
    }

    populating--;

    select(0);
  }

  public int getSelectedIndex()
  {
    return combobox.getSelectedIndex();
  }

  public E getSelected()
  {
    int index = getSelectedIndex();
    if(index < 0) return null;
    return listModel.getElementAt(index);
  }

  public E next()
  {
    if(listModel.getSize() == 0) return null;
    select((getSelectedIndex() + 1) % listModel.getSize());
    return getSelected();
  }

  public E prev()
  {
    if(listModel.getSize() == 0) return null;
    int index = getSelectedIndex() - 1;
    if(index < 0) index = listModel.getSize() - 1;
    select(index);
    return getSelected();
  }

  public void select(int index)
  {
    if(index < 0) return;
    combobox.setSelectedIndex(index);
  }

  public void select(String value)
  {
    select(listModel.indexOf(value));
  }
}
