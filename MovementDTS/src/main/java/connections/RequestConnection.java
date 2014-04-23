package connections;

import serializers.Serializer;

import javax.jms.*;
import javax.naming.InitialContext;
import java.util.Properties;

/**
 * Created by geh on 4-4-14.
 * This class is not thread safe! If you want send requests with multiple threads
 * use one RequestConnection for each thread.
 */
public class RequestConnection extends MovementConnection
{
    /***
     * Used to establish a basic syncrhonous request end of a request-reply channel.
     * @param props these are the settings for connecting to a remote jms server. set to null
     *              to use a local server
     * @param factoryName jndi name of the queuefactory on the application server
     * @param sendTo jndi name of the queue this connection sends to
     */
    public RequestConnection(Properties props, String factoryName, String sendTo)
    {
        this.serializer = new Serializer();

        try
        {
            if(props == null)
            {
                this.context = new InitialContext();
            }
            else
            {
                this.context = new InitialContext(props);
            }
            this.factory = (ConnectionFactory)this.context.lookup(factoryName);
            this.connection = this.factory.createConnection();

            this.session = this.connection.createSession();

            this.sendTo = (Destination)this.context.lookup(sendTo);
            this.producer = this.session.createProducer(this.sendTo);
            this.listenTo = this.session.createTemporaryQueue();
            this.consumer = this.session.createConsumer(this.listenTo);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public String send(String rawRequest)
    {
        String rawReply = "";
        try
        {
            TextMessage request = this.session.createTextMessage();
            request.setJMSReplyTo(this.listenTo);
            request.setText(rawRequest);
            this.producer.send(request);

            int tries = 0;
            TextMessage reply = null;
            while(reply == null && tries < 3)
            {
                reply = (TextMessage)this.consumer.receive(5000);
                tries++;
            }
            if(reply != null)
            {
                rawReply = reply.getText();
            }

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            return rawReply;
        }
    }
}