import { NativeModules, NativeEventEmitter } from 'react-native';

const RNIronSource = NativeModules.RNIronSource;
const IronSourceEventEmitter = new NativeEventEmitter(RNIronSource);

const defaultOptions = {
  validateIntegration: false,
};

const eventNames = [
  'impressionDataDidSucceed'
];

const eventHandlers = eventNames.reduce((result, eventName) => {
  result[eventName] = new Map();
  return result;
}, {});

const addEventListener = (type, handler) => {
  const handlers = eventHandlers[type];
  if (!handlers) {
    console.warn(`Event with type ${type} does not exist.`);
    return;
  }

  if (handlers.has(handler)) {
    console.warn(`Event with type ${type} and handler has already been added.`);
    return;
  }
  
  handlers.set(handler, IronSourceEventEmitter.addListener(type, handler));
};

const removeEventListener = (type, handler) => {
  if (!eventHandlers[type] || !eventHandlers[type].has(handler)) {
    return;
  }
  eventHandlers[type].get(handler).remove();
  eventHandlers[type].delete(handler);
};

const removeAllListeners = () => {
  const count = eventNames.length;
  for (let i = 0; i < count; i++) {
    IronSourceEventEmitter.removeAllListeners(eventNames[i]);
  }
};

module.exports = {
  ...RNIronSource,
  initializeIronSource: (key, userId, options) => RNIronSource.initializeIronSource(key, userId, {
    ...defaultOptions,
    ...options,
  }),
  addEventListener,
  removeEventListener,
  removeAllListeners
};
