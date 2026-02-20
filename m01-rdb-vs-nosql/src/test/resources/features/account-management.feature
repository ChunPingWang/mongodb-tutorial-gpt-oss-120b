Feature: 銀行帳戶管理

  Scenario: 開立新帳戶
    Given 客戶 "張三" 已通過 KYC 驗證
    When 開立活期存款帳戶 初始餘額 10000 元
    Then 帳戶狀態為 ACTIVE
    And 帳戶餘額為 10000 元

  Scenario: 帳戶存款
    Given 帳戶 "A001" 餘額 10000 元
    When 存入 5000 元
    Then 帳戶餘額為 15000 元

  Scenario: 帳戶提款
    Given 帳戶 "A001" 餘額 10000 元
    When 提款 3000 元
    Then 帳戶餘額為 7000 元

  Scenario: 餘額不足提款失敗
    Given 帳戶 "A001" 餘額 1000 元
    When 提款 2000 元
    Then 提款失敗並顯示餘額不足

  Scenario: 帳戶凍結
    Given 帳戶 "A001" 狀態為 ACTIVE
    When 因可疑交易執行凍結
    Then 帳戶狀態為 FROZEN
    And 無法執行提款操作
