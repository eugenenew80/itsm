package kz.kegoc.bln.gateway.oic;

public class OicConfig {
    private OicConfig() {}

    public static OicConfig defaultConfig() {
        return new OicConfig.Builder()
            .server1("OIC01UG.CORP.KEGOC.KZ")
            .server2("OIC02UG.CORP.KEGOC.KZ")
            .port(1433)
            .user("bln")
            .pass("123456")
            .masterDb("MASTER")
            .oicDb("OICDB")
            .build();
    }


    public String buildUrlMaster(ServerType serverType) {
        return "jdbc:sqlserver://" + (serverType == ServerType.SERVER1 ? server1 : server2) + ":" + port + ";user=" + user + ";" + "password=" + pass + ";databasename=" + masterDb;
    }

    public String buildUrlOIC(ServerType serverType) {
        return "jdbc:sqlserver://" + (serverType == ServerType.SERVER1 ? server1 : server2) + ":" + port + ";user=" + user + ";" + "password=" + pass + ";databasename=" + oicDb;
    }

    public static class Builder {
        private String server1;
        private String server2;
        private int port;
        private String user;
        private String pass;
        private String masterDb;
        private String oicDb;

        public Builder server1(String server1) {
            this.server1 = server1;
            return this;
        }

        public Builder server2(String server2) {
            this.server2 = server2;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public Builder pass(String pass) {
            this.pass = pass;
            return this;
        }

        public Builder masterDb(String masterDb) {
            this.masterDb = masterDb;
            return this;
        }

        public Builder oicDb(String oicDb) {
            this.oicDb = oicDb;
            return this;
        }

        public OicConfig build() {
            OicConfig config = new OicConfig();
            config.server1 = this.server1;
            config.server2 = this.server2;
            config.port = this.port;
            config.user = this.user;
            config.pass = this.pass;
            config.oicDb = this.oicDb;
            config.masterDb = this.masterDb;
            return config;
        }
    }

    private String server1;
    private String server2;
    private int port;
    private String user;
    private String pass;
    private String masterDb;
    private String oicDb;
}
