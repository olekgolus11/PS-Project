package main.classes
import main.data_classes.Config

public class Server {
    private lateinit var config: Config;

    constructor(config: Config) {
        this.config = config;
    }
}