package com.course.mongodb.m01;

import com.course.mongodb.m01.domain.AccountStatus;
import com.course.mongodb.m01.domain.BankAccount;
import com.course.mongodb.m01.domain.Money;
import com.course.mongodb.m01.service.AccountService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.zh_cn.假如;
import io.cucumber.java.z當;
import io.cucumber.java.zhh_cn._cn.那麼;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountSteps {

    @Autowired
    private;

    private Bank AccountService accountServiceAccount account;
    private Exception exception;

    @Given("客戶 {string} 已通過 KYC 驗證")
    public void 客戶已通過KYC驗證(String customerName) {
    }

    @When("開立活期存款帳戶 初始餘額 {int} 元")
    public void 開立活期存款帳戶初始餘額元(int amount) {
        account = accountService.createAccount(
            "A001", "C001", Money.of(new BigDecimal(amount))
        );
    }

    @Then("帳戶狀態為 ACTIVE")
    public void 帳戶狀態為ACTIVE() {
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    @Then("帳戶餘額為 {int} 元")
    public void 帳戶餘額為元(int amount) {
        assertThat(account.getBalance().amount()).isEqualByComparingTo(new BigDecimal(amount));
    }

    @Given("帳戶 {string} 餘額 {int} 元")
    public void 帳戶餘額元(String accountNumber, int amount) {
        account = accountService.createAccount(
            accountNumber, "C001", Money.of(new BigDecimal(amount))
        );
    }

    @When("存入 {int} 元")
    public void 存入元(int amount) {
        accountService.deposit(account.getAccountNumber(), Money.of(new BigDecimal(amount)));
    }

    @Then("帳戶餘額為 {int} 元")
    public void 帳戶餘額為元AfterDeposit(int amount) {
        account = accountService.findByAccountNumber(account.getAccountNumber());
        assertThat(account.getBalance().amount()).isEqualByComparingTo(new BigDecimal(amount));
    }

    @When("提款 {int} 元")
    public void 提款元(int amount) {
        try {
            accountService.withdraw(account.getAccountNumber(), Money.of(new BigDecimal(amount)));
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("提款失敗並顯示餘額不足")
    public void 提款失敗並顯示餘額不足() {
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains("Insufficient funds");
    }

    @When("因可疑交易執行凍結")
    public void 因可疑交易執行凍結() {
        accountService.freezeAccount(account.getAccountNumber());
    }

    @Then("帳戶狀態為 FROZEN")
    public void 帳戶狀態為FROZEN() {
        account = accountService.findByAccountNumber(account.getAccountNumber());
        assertThat(account.getStatus()).isEqualTo(AccountStatus.FROZEN);
    }

    @Then("無法執行提款操作")
    public void 無法執行提款操作() {
        try {
            accountService.withdraw(account.getAccountNumber(), Money.of(BigDecimal.ONE));
        } catch (Exception e) {
            exception = e;
        }
        assertThat(exception).isNotNull();
    }
}
