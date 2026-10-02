module genevent {
    requires commons.validator;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens fr.uga.iut2.genevent.basefournie.vue to javafx.fxml;
    opens fr.uga.iut2.genevent.controller to javafx.fxml;
    opens fr.uga.iut2.genevent.view to javafx.fxml;

    exports fr.uga.iut2.genevent;
    exports fr.uga.iut2.genevent.basefournie;
}
