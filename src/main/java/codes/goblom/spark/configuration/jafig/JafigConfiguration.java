package codes.goblom.spark.configuration.jafig;

import net.burngames.jafig.Jafig;

import java.io.File;

/**
 * Creates a new JafigConfiguration.
 *
 * @author PaulBGD
 */
public abstract class JafigConfiguration {

    private final Jafig jafig;

    public JafigConfiguration(Class<? extends Jafig> type, File file) {
        this.jafig = Jafig.create(type, file);
    }

    public void save() {
        this.jafig.save(this);
    }

}
