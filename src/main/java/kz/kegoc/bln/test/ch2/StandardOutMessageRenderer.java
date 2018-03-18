package kz.kegoc.bln.test.ch2;

public class StandardOutMessageRenderer implements MessageRenderer {
    @Override
    public void render() {
        System.out.println(messageProvider.getMessage());
    }

    @Override
    public void setMessageProvider(MessageProvider messageProvider) {
        this.messageProvider = messageProvider;
    }

    @Override
    public MessageProvider getMessageProvider() {
        return messageProvider;
    }

    private MessageProvider messageProvider;
}
