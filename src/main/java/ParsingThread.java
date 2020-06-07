public class ParsingThread extends Thread {

    @Override
    public void run() {
        while (true){
            if (ParsingService.getInstance().isNewCard()){
                StopGameNotificationTelegramBot.getInstance().sendToAllSubscribers(ParsingService.getInstance().getLastCardUrl());
            }

            try {
                sleep(600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
