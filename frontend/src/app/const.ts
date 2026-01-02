export const FEC_FIELD_TYPE = {
  siren : 'siren',
  dateCloture : 'dateCloture',
  pcg : 'pcg',
  journalCode : 'journalCode',
  journalLib : 'journalLib',
  ecritureNum : 'ecritureNum',
  ecritureDate : 'ecritureDate',
  compteNum : 'mpteNum',
  compteLib : 'compteLib',
  compAuxNum : 'compAuxNum',
  compAuxLib : 'compAuxLib',
  pieceRef : 'pieceRef',
  pieceDate : 'pieceDate',
  ecritureLib : 'ecritureLib',
  ecritureLet : 'ecricotureLet',
  dateLet : 'dateLet',
  validDate : 'validDate',
  montantDevise : 'montantDevise',
  idevise : 'idevise',
  montant : 'montant',
  sens : 'sens',
  dateRglt : 'dateRglt',
  modeRglt : 'modeRglt',
  natOp : 'natOp',
  idClient : 'idClient',
  lettrage : 'lettrage',
  dateLettrage : 'dateLettrage',
  tauxTva : 'tauxTva',
  tvaType : 'tvaType'
}

export const PATTERN = {
  email: /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/,
  password: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#;.:=])[A-Za-z\d@$!%*?&#;.:=]{6,}$/,
  phone: /^(\+\d{1,3}[- ]?)?0[1-5]\d{9}$/,
  mobile: /^(\+\d{1,3}[- ]?)?0[6,7]\d{8}$/,
  siren: /^[0-9 ]{9,12}$/,
  name: /^[\w-\. ]{2,}$/
}


export const STORAGE = {
  token: 'app.token',
  role: 'app.role'
}


export const KEYS = {
  // Remettre les ':' quand les routes dynamiques seront à faire //

  //admin: 'key-admins',
  //companies: 'key-company',
  //users: 'key-users',
  //balance: 'key-balances',
  //journal: 'key-journal',
  //grandLivre: 'key-grand-livres',
  //fec: 'key-fecs',
  client: ':id'
  //rules: 'key-rules'
}


const BASE = {
  auth: 'auth',
  dashboard: 'dashboard',
  pcgs: 'pcgs',
  admin: 'admin',
  users: 'users',
  rules: 'rules',
  clients: 'clients',
  companies: 'clients/:idClient/companies',
  annuals: 'clients/:idClient/companies/:idCompany/annuals',
  accounting: 'clients/:idClient/companies/:idCompany/annuals/:idAnnual/accounting',
  fecs: 'clients/:idClient/companies/:idCompany/annuals/:idAnnual/fecs',
  controls: 'clients/:idClient/companies/:idCompany/annuals/:idAnnual/controls/all',
  balance: 'balance',
  grandLivre: 'grand-livre',
  journal: 'journal',
  subscriptions: 'subscriptions',
  uploaded: 'clients/:idClient/uploaded'
}


export const URLS = {
  api: '/api',
  auth: {
    base: BASE.auth,
    signin: 'sign-in',
    signup: 'sign-up',
    signout: 'sign-out',
    connected: 'connected',
    verifyEmail: 'verify-email',
    resetPwd: 'reset-pwd',
    users: 'users'
  },
  dashboard: {
    base: BASE.dashboard,
    admin: `admin`,
    tax_specialist: `tax_specialist`,
    support: `support`,
    key_user: `key_user`,
    user: `user`,
    anonymous: `anonymous`
  },
  users: {
    base: BASE.users,
    users: undefined,
    id: ':idUser',
    adup: 'adup'
  },
  clients: {
    base: BASE.clients,
    clients: undefined,
    companies: ':idClient/companies',
    download: ':idClient/download/:idAnnual',
    employees: ':idClient/employees',
    queries: ':idClient/queries',
    id: ':idClient',
    idQuery: ':idClient/queries/:idQuery',
    keyClients: KEYS.client,
    list: 'list',
    profile: ':idClient/profile',
    subscribe: ':idClient/subscriptions/subscribe',
    subscription: ':idClient/subscriptions',
    upload: ':idClient/upload',
    uploaded : ':idClient/uploaded'
  },
  companies: {
    base: BASE.companies,
    id: ':idCompany'
  },
  annuals: {
    base: BASE.annuals,
    fecs: ':idAnnual/fecs',
    reports: ':idAnnual/reports/all',
    id: ':idAnnual'
  },
  admin: {
    base: BASE.admin
  },
  fecs: {
    base: BASE.fecs,
    files: 'files',
    add: 'add',
    fecs: 'fecs'
  },
  rules: {
    base: BASE.rules,
    add: 'add',
    list: 'list',
    id: ':idRule',
    config: ':idRule/config',
    tax_specialist: `tax_specialist`,
    support: `support`,
    key_user: `key_user`,
    user: `user`,
    anonymous: `anonymous`
  },
  balance: {
    base: BASE.balance
  },
  grandLivre: {
    base: BASE.grandLivre
  },
  journal: {
    base: BASE.journal
  },
  accounting: {
    base: BASE.accounting,
    balance: 'balance',
    'grand-livre': 'grand-livre',
    journal: 'journal'
  },
  pcgs: {
    base: BASE.pcgs,
    pcgs: undefined
  },
  subscriptions: {
    base: BASE.subscriptions,
    id: ':idClient',
    config: 'config',
    subscribe: ':idClient/subscribe/',
    unsubscribe: ':idClient/unsubscribe/:idSubscribe',
    upgrade: ':idClient/upgrade/:idSubscribe'
  },
  uploaded: {
    base: BASE.uploaded,
  },
  controls: {
    base: BASE.controls
  }

}


export const ROUTE = ( obj : any, url? : string | null ) => {
  return `/${ PATH ( obj, url ) }`
}


export const PATH = ( obj : any, url? : string | null ) => {
  return `${ obj.base }${ url && obj[url] ? '/' + obj[url] : '' }`
}


export const ROUTE_VARS = ( obj : any, url : string | null, keys : any ) => {
  return VARS ( ROUTE ( obj, url ), keys )
}


export const API = ( obj : any, url : string | null ) => {
  return `/api/${ obj.base }${ url && obj[url] ? '/' + obj[url] : '' }`
}


export const API_VARS = ( obj : any, url : string | null, keys : any ) => {
  return VARS ( API ( obj, url ), keys )
}


export const VARS = ( url : string, keys : any ) => {
  Object.entries ( keys.ids ).forEach ( ( [key, value] ) => {
    url = url.replaceAll ( `:${ key }`, '' + value )
  } )
  return url
}


export const SPLT = ( url : string ) => {
  return url.split ( '/' ).pop ()
}