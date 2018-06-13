package controller;

import bean.Entree;
import bean.EntreeItem;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import java.io.IOException;
import service.EntreeFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import net.sf.jasperreports.engine.JRException;
import service.EntreeItemFacade;

@Named("entreeController")
@SessionScoped
public class EntreeController implements Serializable {

    @EJB
    private service.EntreeItemFacade entreeItemFacade;
    @EJB
    private service.EntreeFacade ejbFacade;
    private List<Entree> items = null;
    private Entree selected;

    private EntreeItem entreeItem;
    private List<EntreeItem> entreeItems;

    public void generatePdf() throws JRException, IOException {
        entreeItemFacade.generatePdf();
        FacesContext.getCurrentInstance().responseComplete();
    }

    public void findByEntree(Entree entree) {
        entreeItems = (entreeItemFacade.findByEntree(entree));
    }

    public void remove(Entree entree) {
        ejbFacade.remove(entree);
        entreeItems = null;
        int index = getItems().indexOf(entree);
        if (index != -1) {
            getItems().remove(index);
        }
    }

    public void add() {
        entreeItemFacade.add(getEntreeItem(), getEntreeItems());
    }

    public void save() {
        ejbFacade.save(getSelected(), getEntreeItems());
        initAttribute();
    }

    public void reset() {
        initAttribute();
    }

    private void initAttribute() {
        setSelected(null);
        setEntreeItem(null);
        setEntreeItems(null);
    }

    public EntreeController() {
    }

    public EntreeFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEntreeItemFacade(EntreeItemFacade entreeItemFacade) {
        this.entreeItemFacade = entreeItemFacade;
    }

    public EntreeItem getEntreeItem() {
        if (entreeItem == null) {
            entreeItem = new EntreeItem();
        }
        return entreeItem;
    }

    public void setEntreeItem(EntreeItem entreeItem) {
        this.entreeItem = entreeItem;
    }

    public List<EntreeItem> getEntreeItems() {
        if (entreeItems == null) {
            entreeItems = new ArrayList();
        }
        return entreeItems;
    }

    public void setEntreeItems(List<EntreeItem> entreeItems) {
        this.entreeItems = entreeItems;
    }

    public Entree getSelected() {
        if (selected == null) {
            selected = new Entree();
        }
        return selected;
    }

    public void setSelected(Entree selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private EntreeFacade getFacade() {
        return ejbFacade;
    }

    public Entree prepareCreate() {
        selected = new Entree();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("EntreeCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("EntreeUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("EntreeDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Entree> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Entree getEntree(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Entree> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Entree> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Entree.class)
    public static class EntreeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EntreeController controller = (EntreeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "entreeController");
            return controller.getEntree(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Entree) {
                Entree o = (Entree) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Entree.class.getName()});
                return null;
            }
        }

    }

}
