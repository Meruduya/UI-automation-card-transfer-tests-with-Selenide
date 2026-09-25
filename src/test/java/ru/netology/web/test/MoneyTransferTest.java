package ru.netology.web.test;

import org.junit.jupiter.api.*;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashBoardPage;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyTransferTest {

    private DashBoardPage dashboardPage;
    private DataHelper.CardInfo firstCard;
    private DataHelper.CardInfo secondCard;
    private int initialBalance1;
    private int initialBalance2;

    @BeforeEach
    void setup() {
        open("http://127.0.0.1:9999");

        var authInfo = DataHelper.getAutoInfo();
        var loginPage = new LoginPage();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);

        firstCard = DataHelper.getFirstCardInfo();
        secondCard = DataHelper.getSecondCardInfo();
        initialBalance1 = dashboardPage.getCardBalance(firstCard);
        initialBalance2 = dashboardPage.getCardBalance(secondCard);
    }

    // Перевод денег с первой карты на вторую
    @Test
    void shouldTransferMoneyFromFirstCardToSecond() {
        int amount = Math.max(1, initialBalance1 / 4);

        var transferPage = dashboardPage.selectCard(secondCard);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCard);

        assertEquals(initialBalance1 - amount, dashboardPage.getCardBalance(firstCard));
        assertEquals(initialBalance2 + amount, dashboardPage.getCardBalance(secondCard));
    }

    // Перевод денег со второй карты на первую
    @Test
    void shouldTransferMoneyFromSecondCardToFirst() {
        int amount = Math.max(1, initialBalance2 / 4);

        var transferPage = dashboardPage.selectCard(firstCard);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), secondCard);

        assertEquals(initialBalance1 + amount, dashboardPage.getCardBalance(firstCard));
        assertEquals(initialBalance2 - amount, dashboardPage.getCardBalance(secondCard));
    }

    // Перевод суммы больше остатка
    @Disabled("Баг #1: перевод сверх баланса выполняется, баланс уходит в минус")
    @Test
    void shouldGetErrorWhenTransferringMoreThanAvailable() {
        int amount = initialBalance1 + 1; // на 1 рубль больше, чем есть
        var transferPage = dashboardPage.selectCard(secondCard);
        transferPage.makeValidTransfer(String.valueOf(amount), firstCard);
        transferPage.findErrorMessage("Выполнена попытка перевода суммы, превышающей остаток на карте списания");
    }

    // Перевод отклоняется из-за превышения лимита средств, а баланс остаётся тем же
    @Disabled("Баг #2: перевод сверх баланса выполняется, баланс уходит в минус")
    @Test
    void shouldKeepBalanceUnchangedAfterFailedTransfer() {
        int amount = initialBalance1 + 500; // заведомо больше, чем доступно

        var transferPage = dashboardPage.selectCard(secondCard);
        transferPage.makeValidTransfer(String.valueOf(amount), firstCard);

        dashboardPage = dashboardPage.refreshPage();

        assertEquals(initialBalance1, dashboardPage.getCardBalance(firstCard));
        assertEquals(initialBalance2, dashboardPage.getCardBalance(secondCard));
    }

    // Перевод 0 рублей
    @Test
    void shouldTransferZeroAmount() {
        var transferPage = dashboardPage.selectCard(secondCard);
        dashboardPage = transferPage.makeValidTransfer("0", firstCard);

        assertEquals(initialBalance1, dashboardPage.getCardBalance(firstCard));
        assertEquals(initialBalance2, dashboardPage.getCardBalance(secondCard));
    }
}
