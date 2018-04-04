/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sinc.hinc.global.client.RelationshipManagement;

/**
 * @author hungld
 */
public class NetworkGraphGenerator {

//    public String generateGraph(List<SoftwareDefinedGateway> gateways, List<VNF> vnfs) {
//        return generateGraph(getListOfComponents(gateways, vnfs));
//    }
//
//    private String generateGraph(List<NwComponent> components) {
//        Map<String, NwDiagChild> children = new HashMap<>();
//
//        for (NwComponent compo : components) {
//            String subnetName = compo.getSubnet() + ".x.x.x";
//            NwDiagChild child = children.get(subnetName);
//            if (child == null) {
//                child = new NwDiagChild(subnetName, subnetName);
//                children.put(subnetName, child);
//            }
//            child.hasComponent(compo);
//        }
//        NwDiag diag = new NwDiag();
//        for (String s : children.keySet()) {
//            diag.hasNetwork(children.get(s));
//        }
//        return diag.toString();
//    }
//
//    private List<NwComponent> getListOfComponents(List<SoftwareDefinedGateway> gateways, List<VNF> vnfs) {
//        List<NwComponent> components = new ArrayList<>();
//
//        for (SoftwareDefinedGateway gw : gateways) {
//            for (CloudConnectivity con : gw.getConnectivity()) {
//                // TODO: this subnet is hack, need collect real subnet
//                System.out.println("IP is: " + con.getIP() + ". can it have subnet?");
//                if (!con.getIP().isEmpty()) {
//                    String subnet = con.getIP().substring(0, con.getIP().indexOf("."));
//                    components.add(new NwComponent(gw.getName(), con.getIP(), subnet, NetworkComponentType.Gateway));
//
//                }
//            }
//        }
//        System.out.println("Load gateway done: " + components.size() + " items");
//
//        for (VNF vnf : vnfs) {
//            for (Capability capa : vnf.getConnectivities()) {
//                CloudConnectivity con = (CloudConnectivity) capa;
//                // TODO: this subnet is hack, need collect real subnet
//                System.out.println("IP is: " + con.getIP() + ". can it have subnet?");
//                if (!con.getIP().isEmpty()) {
//                    String subnet = con.getIP().substring(0, con.getIP().indexOf("."));
//                    components.add(new NwComponent(vnf.getName(), con.getIP(), subnet, NetworkComponentType.Router));
//
//                }
//            }
//        }
//
//        System.out.println("Load Gateway + NVF done: " + components.size() + " items");
//        return components;
//    }
//
//    public enum NetworkComponentType {
//        Gateway, Router
//    }
//
//    public static class NwDiag {
//
//        List<NwDiagChild> children = new ArrayList<>();
//
//        @Override
//        public String toString() {
//            StringBuilder sb = new StringBuilder("nwdiag{\n");
//            for (NwDiagChild nw : children) {
//                sb.append(nw.toString() + "\n");
//            }
//            sb.append("}");
//            return sb.toString();
//        }
//
//        public NwDiag hasNetwork(NwDiagChild child) {
//            this.children.add(child);
//            return this;
//        }
//
//        public NwDiag hasNetworks(List<NwDiagChild> children) {
//            this.children.addAll(children);
//            return this;
//        }
//
//    }
//
//    public static class NwDiagChild {
//
//        String name;
//        String address;
//        List<NwComponent> components = new ArrayList<>();
//
//        public NwDiagChild(String name, String address) {
//            this.name = name;
//            this.address = address;
//        }
//
//        public NwDiagChild hasComponent(NwComponent component) {
//            this.components.add(component);
//            return this;
//        }
//
//        @Override
//        public String toString() {
//            StringBuilder sb = new StringBuilder("  network " + name + "{ \n");
//            if (address != null) {
//                sb.append("    address = \"" + address + "\";\n");
//            }
//            for (NwComponent s : components) {
//                sb.append(s + "\n");
//            }
//            sb.append("  }");
//            return sb.toString();
//        }
//    }
//
//    public static class NwComponent {
//
//        String name;
//        String address;
//        String subnet;
//        NetworkComponentType type;
//
//        public NwComponent(String name, String address, String subnet, NetworkComponentType type) {
//            this.name = name;
//            this.address = address;
//            this.subnet = subnet;
//            this.type = type;
//        }
//
//        @Override
//        public String toString() {
//            if (address == null) {
//                return "    " + name + ";";
//            } else {
//                return "    " + name + " [address = \"" + address + "\"];";
//            }
//        }
//
//        public String getSubnet() {
//            return subnet;
//        }
//
//        public NetworkComponentType getType() {
//            return type;
//        }
//
//    }

}