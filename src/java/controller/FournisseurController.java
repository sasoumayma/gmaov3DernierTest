package controller;

import bean.Fournisseur;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import java.io.IOException;
import service.FournisseurFacade;

import java.io.Serializable;
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

@Named("fournisseurController")
@SessionScoped
public class FournisseurController implements Serializable {

    @EJB
    private service.FournisseurFacade ejbFacade;
    private List<Fournisseur> items = null;
    private Fournisseur selected;
    
    //
     public void save() {
        ejbFacade.create(getSelected());
        items=ejbFacade.findAll();
        initAttribute();
    }
      private void initAttribute() {
        setSelected(null);
        
    }
    //
    
    public void generatePdf() throws JRException, IOException{
            ejbFacade.generatePdf();
            FacesContext.getCurrentInstance().responseComplete();
        }

    public FournisseurController() {
    }

    public Fournisseur getSelected() {
        if (selected == null) {
            selected = new Fournisseur();
        }
        return selected;
    }

    public void setSelected(Fournisseur selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private FournisseurFacade getFacade() {
        return ejbFacade;
    }

    public Fournisseur prepareCreate() {
        selected = new Fournisseur();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("FournisseurCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("FournisseurUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("FournisseurDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Fournisseur> getItems() {
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

    public Fournisseur getFournisseur(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Fournisseur> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Fournisseur> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Fournisseur.class)
    public static class FournisseurControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            FournisseurController controller = (FournisseurController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "fournisseurController");
            return controller.getFournisseur(getKey(value));
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
            if (object instanceof Fournisseur) {
                Fournisseur o = (Fournisseur) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Fournisseur.class.getName()});
                return null;
            }
        }

    }

}
