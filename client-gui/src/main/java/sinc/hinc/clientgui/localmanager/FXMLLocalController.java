/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sinc.hinc.clientgui.localmanager;

import sinc.hinc.clientgui.UserSettings;
import sinc.hinc.clientgui.globalmanager.HincMetaModel;
import sinc.hinc.clientgui.globalmanager.GenericMetaData;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import sinc.hinc.common.API.HINCGlobalAPI;
import sinc.hinc.common.API.HINCManagementAPI;
import sinc.hinc.common.metadata.HincLocalMeta;
import sinc.hinc.model.VirtualComputingResource.Capability.Capability;
import sinc.hinc.model.VirtualComputingResource.Capability.CapabilityType;
import sinc.hinc.model.VirtualComputingResource.Capability.Concrete.ControlPoint;
import sinc.hinc.model.VirtualComputingResource.Capability.Concrete.DataPoint;
import sinc.hinc.model.VirtualComputingResource.SoftwareDefinedGateway;
import sinc.hinc.model.VirtualNetworkResource.VNF;

/**
 *
 * @author hungld
 */
public class FXMLLocalController implements Initializable {

    HINCGlobalAPI rest = (HINCGlobalAPI) JAXRSClientFactory.create(UserSettings.getDefaultEndpoint(), HINCGlobalAPI.class, Collections.singletonList(new JacksonJaxbJsonProvider()));
    HINCManagementAPI mngAPI = (HINCManagementAPI) JAXRSClientFactory.create(UserSettings.getDefaultEndpoint(), HINCManagementAPI.class, Collections.singletonList(new JacksonJaxbJsonProvider()));

    HincMetaModel hincLocalInfo;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Button btnResourceManager;

    @FXML
    private Button btnQueryManager;

    @FXML
    private void handleButtonLocalResourceManager() {
        mainPane.getChildren().clear();
        System.out.println("Handling button Local resource manager");
        Label label = new Label("Gateway " + hincLocalInfo.uuidProperty().get());
        label.setFont(new Font(20));

        //anchorNodeIntoPane(label, 10.0, 10.0, 0.0, 0.0);
        AnchorPane.setTopAnchor(label, 10.0);
        AnchorPane.setLeftAnchor(label, 10.0);

        mainPane.getChildren().add(label);
        TableView table = showMetaData();
        anchorNodeIntoPane(table, 50.0, 0.0, 0.0, 0.0);
        mainPane.getChildren().add(table);

        btnResourceManager.requestFocus();
    }

    @FXML
    private void handleButtonQueryManager() {
        mainPane.getChildren().clear();

        // a button to query data point, control point
        System.out.println("Handling button query manager");
        Label label = new Label("Gateway " + hincLocalInfo.uuidProperty().get());

        label.setFont(new Font(20));
        AnchorPane.setTopAnchor(label, 10.0);
        AnchorPane.setLeftAnchor(label, 10.0);
        mainPane.getChildren().add(label);

        Button refreshBtn = new Button("Query the gateway");
        AnchorPane.setTopAnchor(refreshBtn, 50.0);
        AnchorPane.setLeftAnchor(refreshBtn, 10.0);

        final Label infoLabel = new Label("The data is load from cache!");
        mainPane.getChildren().add(infoLabel);
        AnchorPane.setTopAnchor(infoLabel, 55.0);
        AnchorPane.setLeftAnchor(infoLabel, 170.0);

        List<HincLocalMeta> metas = mngAPI.queryHINCLocal(0);
//        InfoSourceSettings sourceSettings = null;
//        for (HincMeta meta1 : metas) {
//            System.out.println("Now checking Delise (" + meta1.getUuid() + ") to match with table data:" + deliseInfo.uuidProperty().get());
//            if (meta1.getUuid().equals(deliseInfo.uuidProperty().get())) {
//                System.out.println("Yes it matches");
//                sourceSettings = InfoSourceSettings.fromJson(meta1.getSettings());
//            }
//        }

        refreshBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Reload the list");
                infoLabel.setText("Querying information ... ");

                System.out.println("Querying individual gateway information, id: " + hincLocalInfo.uuidProperty().get());
                List<SoftwareDefinedGateway> gateways = rest.querySoftwareDefinedGateways(2000, hincLocalInfo.uuidProperty().get());
                if (gateways != null && !gateways.isEmpty()) {
                    showGatewayDetails(gateways.get(0));
                }

                infoLabel.setText("The gateway information is updated at: " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
            }
        });
        mainPane.getChildren().add(refreshBtn);

        showGatewayDetails(getGatewayFromCache(hincLocalInfo.uuidProperty().get()));

    }

    private SoftwareDefinedGateway getGatewayFromCache(String uuid) {
        List<SoftwareDefinedGateway> gws = rest.querySoftwareDefinedGateways(0, null);
        if (gws == null) {
            return null;
        }
        for (SoftwareDefinedGateway g : gws) {
            System.out.println("g.getUUID: " + g.getUuid());
            System.out.println("meta.uuid: " + hincLocalInfo.uuidProperty().get() + "\n");
            if (g.getUuid().equals(hincLocalInfo.uuidProperty().get())) {
                return g;
            }
        }
        return null;
    }

    private void showGatewayDetails(SoftwareDefinedGateway gw) {
        System.out.println("Showing gateway details !!!!!!!!!!!!!");
        VBox plane = new VBox();

        AnchorPane.setTopAnchor(plane, 100.0);
        AnchorPane.setLeftAnchor(plane, 0.0);
        AnchorPane.setRightAnchor(plane, 0.0);
        AnchorPane.setBottomAnchor(plane, 0.0);

//        if (secondScene == null) {
//            secondScene = new Scene(plane);
//        }
//        secondStage.setTitle("Detail gateway information");
//        secondStage.setScene(secondScene);
//        secondStage.initStyle(StageStyle.DECORATED);
//        secondStage.initModality(Modality.NONE);
//        Window priStage = mainPane.getScene().getWindow();
//        secondStage.initOwner(priStage);
        // create datapoint table
        {
            TableView table = new TableView();
            TableColumn nameCol = new TableColumn("name");
            nameCol.setCellValueFactory(new PropertyValueFactory<DataPointTableModel, String>("name"));
            TableColumn resourceIDCol = new TableColumn("resourceID");
            resourceIDCol.setCellValueFactory(new PropertyValueFactory<DataPointTableModel, String>("resourceID"));
            TableColumn descriptionCol = new TableColumn("description");
            descriptionCol.setCellValueFactory(new PropertyValueFactory<DataPointTableModel, String>("description"));
            TableColumn measurementUnitCol = new TableColumn("measurementUnit");
            measurementUnitCol.setCellValueFactory(new PropertyValueFactory<DataPointTableModel, String>("measurementUnit"));
            TableColumn rateCol = new TableColumn("Link");
            rateCol.setCellValueFactory(new PropertyValueFactory<DataPointTableModel, String>("rate"));

            table.getColumns().addAll(nameCol, resourceIDCol, descriptionCol, measurementUnitCol, rateCol);
            Label datapointLabel = new Label("Data points");
            datapointLabel.setFont(new Font(18));

            ObservableList<DataPointTableModel> dataPointTable = FXCollections.observableArrayList();
            if (gw != null) {
                System.out.println("Get the gateway data to view: " + gw.toJson());
                for (DataPoint dp : gw.getDataPoints()) {
                    System.out.println("BUiling table field: " + dp.getName() +","+ dp.getResourceID()+","+ dp.getDescription()+","+ dp.getMeasurementUnit()+","+ dp.getLink());
                    dataPointTable.add(new DataPointTableModel(dp.getName(), dp.getResourceID(), dp.getDescription(), dp.getMeasurementUnit(), dp.getLink()));
                }
            }
            table.setItems(dataPointTable);
            plane.getChildren().addAll(datapointLabel, table);

        }
        // create controlpoint table
        {
            TableView table = new TableView();
            TableColumn nameCol = new TableColumn("name");
            nameCol.setCellValueFactory(new PropertyValueFactory<ControlPointTableModel, String>("name"));
            TableColumn resourceIDCol = new TableColumn("resourceID");
            resourceIDCol.setCellValueFactory(new PropertyValueFactory<ControlPointTableModel, String>("resourceID"));
            TableColumn descriptionCol = new TableColumn("description");
            descriptionCol.setCellValueFactory(new PropertyValueFactory<ControlPointTableModel, String>("description"));
            TableColumn invokeProtocolCol = new TableColumn("invokeProtocol");
            invokeProtocolCol.setCellValueFactory(new PropertyValueFactory<ControlPointTableModel, String>("invokeProtocol"));
            TableColumn referenceCol = new TableColumn("reference");
            referenceCol.setCellValueFactory(new PropertyValueFactory<ControlPointTableModel, String>("reference"));

            table.getColumns().addAll(nameCol, resourceIDCol, descriptionCol, invokeProtocolCol, referenceCol);
            Label label = new Label("Control points");
            label.setFont(new Font(18));

            ObservableList<ControlPointTableModel> controlPointTable = FXCollections.observableArrayList();
            if (gw != null) {
                for (ControlPoint cp : gw.getControlPoints()) {
                    controlPointTable.add(new ControlPointTableModel(cp.getName(), cp.getResourceID(), cp.getDescription(), cp.getInvokeProtocol().toString(), cp.getReference()));
                }
            }
            table.setItems(controlPointTable);

            plane.getChildren().addAll(label, table);
        }

        mainPane.getChildren().add(plane);
//        secondStage.show();
    }

    private void showVNFDetails(VNF vnf) {

    }

    private void anchorNodeIntoPane(Node obj, double top, double left, double right, double bottom) {
        AnchorPane.setTopAnchor(obj, top);
        AnchorPane.setLeftAnchor(obj, left);
        AnchorPane.setRightAnchor(obj, right);
        AnchorPane.setBottomAnchor(obj, bottom);
    }

    private TableView showMetaData() {
        TableView table = new TableView();

        TableColumn uuidCol = new TableColumn("Meta");
        uuidCol.setCellValueFactory(new PropertyValueFactory<HincMetaModel, String>("key"));

        TableColumn ipCol = new TableColumn("Value");
        ipCol.setCellValueFactory(new PropertyValueFactory<HincMetaModel, String>("value"));

        table.getColumns().addAll(uuidCol, ipCol);

        ObservableList<GenericMetaData> data = FXCollections.observableArrayList();
        if (hincLocalInfo != null) {
            data.add(new GenericMetaData("UUID", hincLocalInfo.uuidProperty().get()));
            data.add(new GenericMetaData("IP", hincLocalInfo.ipProperty().get()));
            data.add(new GenericMetaData("Information source ref.", hincLocalInfo.sourceProperty().get()));
            data.add(new GenericMetaData("Information source type", hincLocalInfo.sourceTypeProperty().get()));
            data.add(new GenericMetaData("Adaptor class", hincLocalInfo.adaptorProperty().get()));
        }
        table.setItems(data);
        table.setColumnResizePolicy(new Callback<TableView.ResizeFeatures, Boolean>() {
            @Override
            public Boolean call(TableView.ResizeFeatures p) {
                return true;
            }
        });
        return table;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handleButtonLocalResourceManager();
    }

    public void showDataPointControlPoint() {

//        CacheDelises cache = new CacheDelises();
//        List<HincMeta> metas = cache.loadDelisesCache();
//        InfoSourceSettings sourceSettings = null;
//        for (HincMeta meta1 : metas) {
//            System.out.println("Now checking Delise (" + meta1.getUuid() + ") to match with table data:" + tableData.uuidProperty().get());
//            if (meta1.getUuid().equals(tableData.uuidProperty().get())) {
//                System.out.println("Yes it matches");
//                sourceSettings = InfoSourceSettings.fromJson(meta1.getSettings());
//            }
//        }
//        if (sourceSettings != null) {
//            InfoSourceSettings.InfoSource infoSource = sourceSettings.getSource().get(0);
//            System.out.println("InfoSource.get(0): " + sourceSettings.getSource().get(0).getSettings());
//
//            // TODO: make it configurable
//            QueryManager client = new QueryManager("myClient", "amqp://128.130.172.215", "amqp");
//            if (infoSource.isGatewayResource()) {
//                System.out.println("Querying individual gateway information, id: " + tableData.uuidProperty().get());
////            SoftwareDefinedGateway gateway = client.querySoftwareDefinedGateway(meta.uuid.get());
////            showGatewayDetails(gateway);
//
//                // query information. This is a hack to read from cache        
//                CacheGateway cacheGateway = new CacheGateway();
//                List<SoftwareDefinedGateway> gws = cacheGateway.loadGatewaysCache();
//                for (SoftwareDefinedGateway g : gws) {
//                    System.out.println("g.getUUID: " + g.getUuid());
//                    System.out.println("meta.uuid: " + tableData.uuidProperty().get() + "\n");
//                    if (g.getUuid().equals(tableData.uuidProperty().get())) {
//                        showGatewayDetails(g);
//                    }
//                }
//
//            } else if (infoSource.isVNFResource()) {
//                System.out.println("Querying individual router information, id: " + tableData.uuidProperty().get());
//                VNF vnf = client.queryVNF(tableData.uuidProperty().get());
//                showVNFDetails(vnf);
//            }
//        }
    }

    public HincMetaModel getMetadata() {
        return hincLocalInfo;
    }

    public void setMetadata(HincMetaModel metadata) {
        this.hincLocalInfo = metadata;
    }

}
