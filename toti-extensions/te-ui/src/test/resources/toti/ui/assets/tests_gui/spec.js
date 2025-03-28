// https://playwright.dev/docs/writing-tests#test-isolation
// npx playwright test

// https://stackoverflow.com/a/62508790/8240462

module.exports = (conf)=>{
  const { test, expect } = require('@playwright/test');
  function parseValue(val) {
      if (val === undefined) {
          return 'undefined';
      }
      if (val === null) {
          return 'null';
      }
      if (val === true) {
          return 'true';
      }
      if (val === false) {
          return 'false';
      }
      if (typeof val === 'number') {
          return val;
      }
      if (Array.isArray(val)) {
          return JSON.stringify(val);
      }
      if (typeof val === 'object') {
          return JSON.stringify(val);
      }
      return "'" + val + "'";
  }
  const assert = {
    throw: (message)=>{
        throw new Error(message + new Error().stack.replace('Error:', ''));
    },
    areSame: (expected, actual, message = '')=>{
        var match = expected === actual;
        if (expected !== null && typeof expected === 'object') {
            // actual is parseValue - state of actual is unknown
            match = JSON.stringify(expected) === parseValue(actual);
        }
        if (!match) {
            assert.throw('areSame fails. ' + message + ' Expected: ' + parseValue(expected) + ", Actual: " + parseValue(actual))
            // throw new Error('areSame fails. ' + message + ' Expected: ' + parseValue(expected) + ", Actual: " + parseValue(actual));
        }
    },
    isNull: (value, message = '')=>{
        if (value !== null) {
            assert.throw('isNull fails. ' + message + " Value: " + parseValue(value));
            // throw new Error('isNull fails. ' + message + " Value: " + parseValue(value));
        }
    },
    isNotNull: (value, message = '')=>{
        if (value === null) {
            assert.throw('isNotNull fails. ' + message + " Value: " + parseValue(value));
            // throw new Error('isNotNull fails. ' + message + " Value: " + parseValue(value));
        }
    },
    isTrue: (value, message = '')=>{
        if (value !== true) {
            assert.throw('isTrue fails. ' + message + " Value: " + parseValue(value));
            // throw new Error('isTrue fails. ' + message + " Value: " + parseValue(value));
        }
    },
    isFalse: (value, message = '')=>{
        if (value !== false) {
            assert.throw('isFalse fails. ' + message + " Value: " + parseValue(value));
            // throw new Error('isFalse fails. ' + message + " Value: " + parseValue(value));
        }
    }
  };

  function runTest(spec, create, verify) {
      test(spec.name, async ({ page }) => {
          await page.goto('file://' + __dirname + '/test.html');
          await page.addScriptTag({
            content: 'var initTest = '+ create.toString() +';'
          });
          const consoleMessages = [];
          page.on("console", (message) => {
            consoleMessages.push(message.type().toUpperCase() + ": " + message.text());
          });
          page.on("pageerror", (err) => {
            consoleMessages.push("ERROR: " + err.message);
          });

          var instance = await page.evaluateHandle((conf)=>{
            var item = initTest(conf);
            window.item = item;
            document.getElementById('target').appendChild(item.getContainer());
            item.getContainer().setAttribute('id', 'testedElement');
            return item;
          }, spec.conf);

          var callInstance = (method, ...params)=>{
            return page.evaluate((data)=>{
              /*if (!data.handler.hasOwnProperty(data.method)) {
                  throw new Error('Handler miss method: ' + data.method);
              }*/
              return data.handler[data.method](...data.params);
            }, {
              handler: instance,
              method: method,
              params: params
            });
          };
          var callContainer = (method, ...params)=>{
            return page.evaluate((data)=>{
              var property = document.getElementById('testedElement')[data.method];
              if (typeof property === 'function') {
                  return property(...data.params);
              }
              return property;
            }, {
              method: method,
              params: params
            });
          };
        //  var locator = await page.locator('#target:first-child');
          var locator = await page.locator('#testedElement');
          await expect(locator).toHaveCount(1);
          await expect(locator).toBeAttached();

          await verify(expect, assert, page, callInstance, callContainer, locator);

          // call something in instance
          // var x = await call('getType');
          /*
          // evaluate
          var container = await page.evaluateHandle(()=>{
            return document.getElementById('target').childNodes[0];
          });
          var x = await page.evaluate((data)=>{
            return "ev";
          }, {
            handler: handler
          });
          */

          consoleMessages.forEach((m)=>{
            console.log(m);
          });
      });
  }
  function iterateTests(tests, fCreate) {
      tests.forEach((item)=>{
          var create = fCreate;
          if (item.hasOwnProperty('create')) {
              create = item.create;
          }
          if (item.type === 'test') {
              if (!item.hasOwnProperty('verify')) {
                // ignore
              } else if (typeof item.verify === 'function') {
                  runTest(item, create, item.verify);
              } else {
                  test.describe(item.name, ()=>{
                    for(const[subName, verify] of Object.entries(item.verify)) {
                        runTest({
                          type: 'test',
                          name: subName,
                          conf: JSON.parse(JSON.stringify(item.conf)),
                        }, create, verify);
                    }
                });
              }
          } else if (item.type === 'group') {
              test.describe(item.name, ()=>{
                  iterateTests(item.tests, create);
              });
          }
      });
  }
  iterateTests(conf.tests, conf.create);
};


// @ts-check
  //await page.screenshot({ path: 'screenshot.png' });
/*
test.describe('two tests', () => {
  test.beforeEach(async ({ page }) => {
    // Go to the starting url before each test.
    await page.goto('https://playwright.dev/');
  });
  test('one', async ({ page }) => {
    // ...
  });

  test('two', async ({ page }) => {
    // ...
  });
});

*/