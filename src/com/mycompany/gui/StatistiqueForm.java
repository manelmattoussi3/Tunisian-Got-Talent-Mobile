/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gui;

import com.codename1.charts.ChartComponent;
import com.codename1.charts.models.CategorySeries;
import com.codename1.charts.renderers.DefaultRenderer;
import com.codename1.charts.renderers.SimpleSeriesRenderer;
import com.codename1.charts.views.PieChart;
import com.codename1.components.ScaleImageLabel;
import com.codename1.components.SpanLabel;
import com.codename1.components.ToastBar;
import com.codename1.ui.Button;
import com.codename1.ui.ButtonGroup;
import com.codename1.ui.Component;
import static com.codename1.ui.Component.BOTTOM;
import static com.codename1.ui.Component.CENTER;
import static com.codename1.ui.Component.LEFT;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Font;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.RadioButton;
import com.codename1.ui.Tabs;
import com.codename1.ui.TextArea;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.layouts.LayeredLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.util.Resources;
import com.mycompany.entites.Evenement;
import com.mycompany.services.EvenementService;
import com.mycompany.services.PublicationService;

/**
 *
 * @author Khawla
 */
public class StatistiqueForm extends BaseForm {
    EvenementService es = new EvenementService();
    double evenements = (double)es.getNbrEvenement();
    double participation = (double)es.getNbrParticpation();
    PublicationService fs = new PublicationService();
    double publications  = (double)fs.getNbrPublication();
    private boolean drawOnMutableImage;
    Font smallFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.SIZE_SMALL, Font.STYLE_PLAIN);
    Font medFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.SIZE_MEDIUM, Font.STYLE_PLAIN);
    Font largeFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.SIZE_LARGE, Font.STYLE_PLAIN);
    Form current;
    BaseForm form;
        public StatistiqueForm(Resources res)  {
        super("Newsfeed", BoxLayout.y());
            current= this;
        Toolbar tb = new Toolbar(true);
        setToolbar(tb);
        getTitleArea().setUIID("Container");
        setTitle("Acceuill");
        getContentPane().setScrollVisible(false);
        super.addSideMenu(res);
        tb.addSearchCommand(e -> {});
        Tabs swipe = new Tabs();
        Label spacer1 = new Label();
        Label spacer2 = new Label();
        addTab(swipe, res.getImage("feedback.jpg"), spacer1, "Bienvenue");
        swipe.setUIID("Container");
        swipe.getContentPane().setUIID("Container");
        swipe.hideTabs();
        ButtonGroup bg = new ButtonGroup();
        int size = Display.getInstance().convertToPixels(1);
        Image unselectedWalkthru = Image.createImage(size, size, 0);
        Graphics g = unselectedWalkthru.getGraphics();
        g.setColor(0xffffff);
        g.setAlpha(100);
        g.setAntiAliased(true);
        g.fillArc(0, 0, size, size, 0, 360);
        Image selectedWalkthru = Image.createImage(size, size, 0);
        g = selectedWalkthru.getGraphics();
        g.setColor(0xffffff);
        g.setAntiAliased(true);
        g.fillArc(0, 0, size, size, 0, 360);
        RadioButton[] rbs = new RadioButton[swipe.getTabCount()];
        FlowLayout flow = new FlowLayout(CENTER);
        flow.setValign(BOTTOM);
        Container radioContainer = new Container(flow);
        for(int iter = 0 ; iter < rbs.length ; iter++) {
            rbs[iter] = RadioButton.createToggle(unselectedWalkthru, bg);
            rbs[iter].setPressedIcon(selectedWalkthru);
            rbs[iter].setUIID("Label");
            radioContainer.add(rbs[iter]);
        }
                
        rbs[0].setSelected(true);
        swipe.addSelectionListener((i, ii) -> {
            if(!rbs[ii].isSelected()) {
                rbs[ii].setSelected(true);
            }
        });
        refreshTheme();
        Component.setSameSize(radioContainer, spacer1, spacer2);
        add(LayeredLayout.encloseIn(swipe, radioContainer));
        ButtonGroup barGroup = new ButtonGroup();
        RadioButton popular = RadioButton.createToggle("Acceuil", barGroup);
        popular.setUIID("SelectBar");
        RadioButton feedback = RadioButton.createToggle("Autre", barGroup);
        feedback.setUIID("SelectBar");
        RadioButton profile = RadioButton.createToggle("Statistique", barGroup);
        profile.setUIID("SelectBar");
        Label arrow = new Label(res.getImage("news-tab-down-arrow.png"), "Container");
        add(LayeredLayout.encloseIn(
                GridLayout.encloseIn(2, popular,profile),
                FlowLayout.encloseBottom(arrow)
        ));
     
        arrow.setVisible(false);
        addShowListener(e -> {
            arrow.setVisible(true);
           
        });
        bindButtonSelection(popular, arrow);
        popular.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                new NewsfeedForm(res).showBack();
            }
        });

        popular.setSelected(true);
        arrow.setVisible(false);
        addShowListener(e -> {
            arrow.setVisible(true);
            updateArrowPosition(profile, arrow);
        });
        bindButtonSelection(profile, arrow);
                profile.addActionListener((e)->{
                  new StatistiqueForm(res).show();
                  
        });
        // special case for rotation
        addOrientationListener(e -> {
            updateArrowPosition(barGroup.getRadioButton(barGroup.getSelectedIndex()), arrow);
        });
        createPieChartForm();
    
        }
    
   
     private void updateArrowPosition(Button b, Label arrow) {
        arrow.getUnselectedStyle().setMargin(LEFT, b.getX() + b.getWidth() / 2 - arrow.getWidth() / 2);
        arrow.getParent().repaint();
     }
    
    private void addTab(Tabs swipe, Image img, Label spacer, String text) {
        int size = Math.min(Display.getInstance().getDisplayWidth(), Display.getInstance().getDisplayHeight());
        if(img.getHeight() < size) {
            img = img.scaledHeight(size);
        }

        if(img.getHeight() > Display.getInstance().getDisplayHeight() / 2) {
            img = img.scaledHeight(Display.getInstance().getDisplayHeight() / 2);
        }
        ScaleImageLabel image = new ScaleImageLabel(img);
        image.setUIID("Container");
        image.setBackgroundType(Style.BACKGROUND_IMAGE_SCALED_FILL);
        Label overlay = new Label(" ", "ImageOverlay");
        Container page1 = 
            LayeredLayout.encloseIn(
                image,
                overlay,
                BorderLayout.south(
                    BoxLayout.encloseY(
                            new SpanLabel(text, "LargeWhiteText"),
                            spacer
                        )
                )
            );

        swipe.addTab("", page1);
    }
    
   private void addButton(Image img,String title,Evenement evv) {
        int height = Display.getInstance().convertToPixels(11.5f);
        int width = Display.getInstance().convertToPixels(14f);
        Button image = new Button(img.fill(width, height));
        image.setUIID("Label");
        Container cnt = BorderLayout.west(image);
        cnt.setLeadComponent(image);
        TextArea ta = new TextArea(title);
        ta.setUIID("NewsTopLine");
        ta.setEditable(false);

  ;       
      
       cnt.add(BorderLayout.CENTER, 
               BoxLayout.encloseY(
                       ta
               ));
       
       image.addActionListener(e -> {
           try{
       //    new AjoutReclamationForm(Resources.getGlobalResources()).show();
           }catch(Exception exx) {
               
           }
               });
        add(cnt);
        image.addActionListener(e -> ToastBar.showMessage(title, FontImage.MATERIAL_INFO));
   }
    private void bindButtonSelection(Button b, Label arrow) {
        b.addActionListener(e -> {
            if(b.isSelected()) {
                updateArrowPosition(b, arrow);
            }
        });
    }
    private DefaultRenderer buildCategoryRenderer(int[] colors) {
        DefaultRenderer renderer = new DefaultRenderer();
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setMargins(new int[]{20, 30, 15, 0});
        for (int color : colors) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(color);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    protected CategorySeries buildCategoryDataset(String title, double valuesRec,double valuesFeed,double valuesPart) {
    CategorySeries series = new CategorySeries(title);
        
        
      
        int k = 0;
//        for (double value : values) {           
            series.add("Evenement " , valuesRec);
            series.add("Publication" , valuesFeed);
            series.add("Participation" , valuesPart);
//        }
        return series;
    }

    public void createPieChartForm() {
       double total  = publications + evenements +participation;
       // Generate the values
       //float pourcentage = (li*100)/li.get(1);
            double pourcentage = (evenements*100)/total;
                                    double pourcentage2 = (participation*100)/total;

                        double pourcentage1 = (publications*100)/total;
        int[] colors = new int[]{0xf4b342, 0x52b29a,0x0000ff};
        DefaultRenderer renderer = buildCategoryRenderer(colors);
        renderer.setLabelsTextSize(40);
        renderer.setLabelsColor(0x000000);
        renderer.setZoomButtonsVisible(true);
        renderer.setZoomEnabled(true);
        renderer.setChartTitleTextSize(20);
        renderer.setDisplayValues(true);
        renderer.setShowLabels(true);
        SimpleSeriesRenderer r = renderer.getSeriesRendererAt(0);
        r.setHighlighted(true);
        // Create the chart ... pass the values and renderer to the chart object.
        PieChart chart = new PieChart(buildCategoryDataset("Project budget", pourcentage,pourcentage1,pourcentage2), renderer);
        // Wrap the chart in a Component so we can add it to a form
        ChartComponent c = new ChartComponent(chart);
        String[] messages = {
            "Statistique Evenement % Publication % Participation "            
        };
        
        SpanLabel message = new SpanLabel(messages[0], "WelcomeMessage");
        Container cnt = BorderLayout.center(message);
         cnt.setUIID("Conatiner");
           add(cnt);

           add(c);
           }
    
}
    

