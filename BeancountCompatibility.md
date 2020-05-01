# Beancount Compatibility Notes

* This app currently use a alternative Beancount file parser implementation
    * the parser is still WIP, it might parse differently compared to the Beancount command-line tool and Fava, for some corner cases
    * `txn` is not parsed correctly, you can use the flag `*` instead
    * TODO compound amount will result in crash
* Supported features
    * view and edit Beancount files, we cannot list all supported features, because the aim is to support all common features one might want
    * updating an item will modify the part of file where it is originally defined
    * newly added items will appear in the main file
* Partially supported features
    * number expressions is calculated, so editing and saving a transaction with number expressions will write out the calculated result
    * tags defined by tag stack is parsed and added to transactions, saving a transaction with tags defined by tag stack will cause the tag appear also in transaction itself
    * metadata on transaction, currency and account is displayed but not editable
    * metadata on posting, price entry and balance entry is not displayed, but will be saved without change
    * transactions: GUI only support modify cost specification without a tag or date (they are almost only needed if you use STRICT booking)
    * options supported
        * `booking_method`
        * `name_assets`, `name_liabilities`, etc.
        * `title`, `operating_currency` (currently not actually used)
* Not yet implemented, but in plan
    * saving items will lost their inline comments
    * account: open date and close date is not interpreted, saving will not change them
    * `document` and `event`
    * price and balance is only displayed but not editable
* Unsupported features
    * `pad` directives is ignored, it is recommended to explicitly pad with transactions
    * ignored directive: `event`, `note`, `query`
    * `option` which are not listed above
    * ignored directives because we don't know how to interpret them: `custom`, `plugin`
    * `include` doesn't support Unix style globs yet -- maybe we should just parse all files in a folder
    * other feature the developer is not aware of -- if you think some feature is important to support it, please open an [issue](https://github.com/Beantamer/app/issues)
* Features not in Beancount
    * we support metadata `time` in transactions and price, `asset-class` in currency
    * some other features is implemented by app-specific metadata, like settings for SMS importers
* Booking is different with Beancount
    * only STRICT, FIFO, LIFO is supported
    * basics should work fine
    * prefer write out postings more explicitly, this app prefer infer less stuff, especially a incomplete amount (e.g. without number: "USD"; without currency: "123") will be treated as no amount specified at all
    * all currency tolerance is fixed at 0.005, we don't plan to infer tolerance, but provide a per-currency settings instead in the future
