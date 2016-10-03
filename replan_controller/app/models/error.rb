class Error
  include ActiveModel::Model

  attr_accessor :code, :message, :fields
end